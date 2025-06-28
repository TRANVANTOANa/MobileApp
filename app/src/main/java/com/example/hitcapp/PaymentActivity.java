package com.example.hitcapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // Đảm bảo import này tồn tại
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvOrderId, tvOrderProducts, tvOrderTime, tvPaymentTotalPrice;
    private EditText edtFullName, edtPhoneNumber, edtAddress, edtNote;
    private Button btnUploadReceipt, btnConfirmPayment;
    private TextView tvSelectedImageName;
    private ImageView qrCodeImage;
    private TextView nap247InfoLabel, nap247Info;
    private LinearLayout llUploadReceipt;

    private RadioGroup rgPaymentMethod;
    private RadioButton rbCash, rbBankTransfer, rbEWallet;

    private double orderTotalPrice;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);

        // Đảm bảo ID này khớp với ID gốc của layout trong activity_payment.xml
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_payment_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Ánh xạ các View ---
        tvOrderId = findViewById(R.id.tvOrderId);
        tvOrderProducts = findViewById(R.id.tvOrderProducts);
        tvOrderTime = findViewById(R.id.tvOrderTime);
        tvPaymentTotalPrice = findViewById(R.id.tvPaymentTotalPrice);

        edtFullName = findViewById(R.id.edtFullName);
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber);
        edtAddress = findViewById(R.id.edtAddress);
        edtNote = findViewById(R.id.edtNote);

        btnUploadReceipt = findViewById(R.id.btnUploadReceipt);
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment);
        tvSelectedImageName = findViewById(R.id.tvSelectedImageName);
        qrCodeImage = findViewById(R.id.qrCodeImage);
        nap247InfoLabel = findViewById(R.id.nap247_info_label);
        nap247Info = findViewById(R.id.nap247_info);
        llUploadReceipt = findViewById(R.id.llUploadReceipt);

        rgPaymentMethod = findViewById(R.id.rgPaymentMethod);
        rbCash = findViewById(R.id.rbCash);
        rbBankTransfer = findViewById(R.id.rbBankTransfer);
        rbEWallet = findViewById(R.id.rbEWallet);

        // --- Lấy tổng tiền từ CartManager (hoặc Intent) ---
        orderTotalPrice = CartManager.getInstance().getTotalPrice();
        tvPaymentTotalPrice.setText(formatCurrency(orderTotalPrice));

        // --- Hiển thị thông tin đơn hàng từ CartManager ---
        tvOrderId.setText(generateOrderId());
        tvOrderProducts.setText(getCartProductsString());
        tvOrderTime.setText(getCurrentDateTime());

        // --- Thiết lập Listener cho RadioGroup phương thức thanh toán ---
        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbBankTransfer) {
                qrCodeImage.setVisibility(View.VISIBLE);
                nap247InfoLabel.setVisibility(View.VISIBLE);
                nap247Info.setVisibility(View.VISIBLE);
                llUploadReceipt.setVisibility(View.VISIBLE);
                Toast.makeText(PaymentActivity.this, "Bạn đã chọn chuyển khoản ngân hàng", Toast.LENGTH_SHORT).show();
            } else {
                qrCodeImage.setVisibility(View.GONE);
                nap247InfoLabel.setVisibility(View.GONE);
                nap247Info.setVisibility(View.GONE);
                llUploadReceipt.setVisibility(View.GONE);
                tvSelectedImageName.setText("Chưa có tệp nào được chọn");
                Toast.makeText(PaymentActivity.this, "Bạn đã chọn thanh toán " + (checkedId == R.id.rbCash ? "Tiền mặt" : "Ví điện tử"), Toast.LENGTH_SHORT).show();
            }
        });

        // Thiết lập trạng thái ban đầu dựa trên phương thức mặc định
        if (rbCash.isChecked()) {
            qrCodeImage.setVisibility(View.GONE);
            nap247InfoLabel.setVisibility(View.GONE);
            nap247Info.setVisibility(View.GONE);
            llUploadReceipt.setVisibility(View.GONE);
        } else if (rbBankTransfer.isChecked()) {
            qrCodeImage.setVisibility(View.VISIBLE);
            nap247InfoLabel.setVisibility(View.VISIBLE);
            nap247Info.setVisibility(View.VISIBLE);
            llUploadReceipt.setVisibility(View.VISIBLE);
        } else { // rbEWallet
            qrCodeImage.setVisibility(View.GONE);
            nap247InfoLabel.setVisibility(View.GONE);
            nap247Info.setVisibility(View.GONE);
            llUploadReceipt.setVisibility(View.GONE);
        }

        // Hiển thị QR Code (ví dụ)
        qrCodeImage.setImageResource(R.drawable.qr_code_placeholder); // Đảm bảo bạn có tài nguyên này

        // --- Thiết lập sự kiện cho nút Tải lên hóa đơn ---
        btnUploadReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        // --- Thiết lập sự kiện cho nút Xác nhận thanh toán ---
        btnConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmPayment();
            }
        });

        // Yêu cầu quyền truy cập bộ nhớ
        requestStoragePermission();
    }

    private String formatCurrency(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return format.format(amount).replace("₫", "đ");
    }

    private String generateOrderId() {
        long timestamp = System.currentTimeMillis();
        return "ORD" + String.valueOf(timestamp).substring(String.valueOf(timestamp).length() - 5);
    }

    private String getCartProductsString() {
        StringBuilder productsList = new StringBuilder();
        if (CartManager.getInstance() != null && CartManager.getInstance().getCartItems() != null) {
            for (CartItem item : CartManager.getInstance().getCartItems()) {
                productsList.append(item.getProduct().getName())
                        .append(" (SL: ").append(item.getQuantity())
                        .append("), ");
            }
        }
        if (productsList.length() > 0) {
            productsList.setLength(productsList.length() - 2);
        } else {
            productsList.append("Giỏ hàng trống");
        }
        return productsList.toString();
    }

    private String getCurrentDateTime() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
            return now.format(formatter);
        } else {
            java.util.Date date = new java.util.Date();
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault());
            return sdf.format(date);
        }
    }

    private void openImageChooser() {
        int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedId != R.id.rbBankTransfer) {
            Toast.makeText(this, "Chỉ có thể tải hóa đơn cho phương thức chuyển khoản ngân hàng.", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh hóa đơn"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            String fileName = getFileName(imageUri);
            tvSelectedImageName.setText(fileName);
            Toast.makeText(this, "Đã chọn ảnh: " + fileName, Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME);
                    if (nameIndex != -1) {
                        result = cursor.getString(nameIndex);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void confirmPayment() {
        hideKeyboard();

        String fullName = edtFullName.getText().toString().trim();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String note = edtNote.getText().toString().trim();
        String selectedImageName = tvSelectedImageName.getText().toString();

        int selectedPaymentMethodId = rgPaymentMethod.getCheckedRadioButtonId();
        RadioButton selectedRadioButton = findViewById(selectedPaymentMethodId);
        String paymentMethod = selectedRadioButton.getText().toString();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin nhận hàng bắt buộc.", Toast.LENGTH_LONG).show();
            return;
        }

        if (selectedPaymentMethodId == R.id.rbBankTransfer && selectedImageName.equals("Chưa có tệp nào được chọn")) {
            Toast.makeText(this, "Vui lòng tải lên hóa đơn chuyển khoản cho phương thức này.", Toast.LENGTH_LONG).show();
            return;
        }

        String orderId = tvOrderId.getText().toString();
        String productsSummary = getCartProductsString();
        String totalAmount = tvPaymentTotalPrice.getText().toString();
        String orderTimestamp = tvOrderTime.getText().toString();

        String orderDetails = "Mã đơn hàng: " + orderId + "\n" +
                "Sản phẩm: " + productsSummary + "\n" +
                "Tổng tiền: " + totalAmount + "\n" +
                "Thời gian đặt: " + orderTimestamp + "\n" +
                "Phương thức TT: " + paymentMethod + "\n" +
                "Tên khách hàng: " + fullName + "\n" +
                "SĐT: " + phoneNumber + "\n" +
                "Địa chỉ: " + address + "\n" +
                "Ghi chú: " + (TextUtils.isEmpty(note) ? "Không có" : note) + "\n" +
                "Hóa đơn đính kèm: " + (selectedImageName.equals("Chưa có tệp nào được chọn") ? "Không có" : selectedImageName);

        Toast.makeText(this, "Đơn hàng của bạn đã được xác nhận và đang chờ xử lý!\n" + orderDetails, Toast.LENGTH_LONG).show();

        CartManager.getInstance().clearCart();

        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void requestStoragePermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 99);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 99);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 99) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Quyền truy cập bộ nhớ đã được cấp.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Quyền truy cập bộ nhớ bị từ chối. Không thể tải hóa đơn.", Toast.LENGTH_LONG).show();
            }
        }
    }
}