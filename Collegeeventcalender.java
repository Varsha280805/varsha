import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;

public class MainActivity extends AppCompatActivity {
    private static final String KEYSTORE_PASSWORD = "password123";
    private static final String KEY_ALIAS = "my_key";

    private KeyStore keyStore;
    private PrivateKey privateKey;
    private PublicKey publicKey;
    private Certificate certificate;

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameEditText = findViewById(R.id.username_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (username.equals("admin") && password.equals("password")) {
                    Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                    // Initialize key store and load private key and certificate
                    initKeyStore();

                    // Encrypt data using public key
                    String encryptedData = encryptData("Hello, world!");

                    // Decrypt data using private key
                    String decryptedData = decryptData(encryptedData);

                    Toast.makeText(MainActivity.this, "Decrypted data: " + decryptedData, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initKeyStore() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null, KEYSTORE_PASSWORD.toCharArray());

            privateKey = (PrivateKey) keyStore.getKey(KEY_ALIAS, KEYSTORE_PASSWORD.toCharArray());
            publicKey = keyStore.getCertificate(KEY_ALIAS).getPublicKey();
            certificate = keyStore.getCertificate(KEY_ALIAS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String encryptData(String data) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);

            byte[] encryptedBytes = cipher.doFinal(data.getBytes());
            return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String decryptData(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);

            byte[] decryptedBytes = cipher.doFinal(android.util.Base64.decode(encryptedData, android.util.Base64.DEFAULT));
            return new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}