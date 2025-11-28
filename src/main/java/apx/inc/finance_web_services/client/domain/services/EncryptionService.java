package apx.inc.finance_web_services.client.domain.services;

public interface EncryptionService {
    String encrypt(String data);
    String decrypt(String encryptedData);
}
