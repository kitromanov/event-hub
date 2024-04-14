package cc.coopersoft.keycloak.phone.providers.representations;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.keycloak.models.utils.KeycloakModelUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.jboss.logging.Logger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenCodeRepresentation {

    private static final Logger logger = Logger.getLogger(TokenCodeRepresentation.class.getName());

    private String id;
    private String phoneNumber;
    private String code;
    private String type;
    private Date createdAt;
    private Date expiresAt;
    private Boolean confirmed;

    public static TokenCodeRepresentation forPhoneNumber(String phoneNumber) {
        TokenCodeRepresentation tokenCode = new TokenCodeRepresentation();
        tokenCode.id = KeycloakModelUtils.generateId();
        tokenCode.phoneNumber = phoneNumber;
        tokenCode.confirmed = false;
        tokenCode.code = "6666";

        String PHONE_PROVIDER_API_KEY = System.getenv("PHONE_PROVIDER_API_KEY");
        try {
            JSONObject requestBody = new JSONObject();
            String api_format_phone = "7" + phoneNumber.substring(phoneNumber.length() - 10);
            requestBody.put("recipient", api_format_phone);
            requestBody.put("id", tokenCode.id);
            requestBody.put("validate", false);

            URL url = new URL("https://lcab.smsprofi.ru/json/v1.0/callpassword/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            conn.setRequestProperty("X-Token", PHONE_PROVIDER_API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{\"recipient\": \"" + api_format_phone + "\",\"id\": \"myId123\",\"tags\": [\"2024\", \"ТЦ Аист\"],\"validate\": false,\"limit\": {\"count\": 3,\"period\": 600}}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            in.close();
            String code = getCodeFromResponse(response.toString());

            if (code != null) {
                tokenCode.setCode(code);
            }
            tokenCode.code = code;
        } catch (Exception e) {
            logger.info("Error sending authentication code");
        }

        return tokenCode;
    }

    private static String getCodeFromResponse(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject result = jsonObject.getJSONObject("result");
        return result.getString("code");
    }
}