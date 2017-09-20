package ch.swissbytes.module.shared.rest.security.dto;


import ch.swissbytes.module.buho.app.role.model.Role;
import com.nimbusds.jwt.JWTClaimsSet;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang.BooleanUtils;

import java.util.List;

public class IdentityDto {


    JSONObject jsonObject;

    public IdentityDto() {
        this.jsonObject = new JSONObject();
    }

    public IdentityDto(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public IdentityDto(String json) {
        this.jsonObject = (JSONObject) JSONValue.parse(json);
    }

    public boolean isEmpty() {
        return jsonObject.isEmpty();
    }

    public String toString() {
        return jsonObject.toString();
    }

    public String toJSONString() {
        return jsonObject.toJSONString();
    }

    public Long getId() {
        return (Long) jsonObject.get("id");
    }

    public void setId(Long id) {
        jsonObject.put("id", id);
    }

    public String getUserName() {
        return (String) jsonObject.get("username");
    }

    public void setUserName(String userName) {
        jsonObject.put("username", userName);
    }

    public String getEmail() {
        return (String) jsonObject.get("email");
    }

    public void setEmail(String email) {
        jsonObject.put("email", email);
    }

    public String getName() {
        return (String) jsonObject.get("name");
    }

    public void setName(String name) {
        jsonObject.put("name", name);
    }

    public String getLastName() {
        return (String) jsonObject.get("lastName");
    }

    public void setLastName(String lastName) {
        jsonObject.put("lastName", lastName);
    }

    public Long getAccountId() {
        return (Long) jsonObject.get("accountId");
    }

    public void setAccountId(Long accountId) {
        jsonObject.put("accountId", accountId);
    }

    public Boolean isCompanyTest() {
        return (Boolean) jsonObject.get("companyTest");
    }

    public Boolean isBuhoAdmin() {
        return (Boolean) jsonObject.get("buhoAdmin");
    }

    public void isBuhoAdmin(Boolean flag) {
        jsonObject.put("buhoAdmin", flag);
    }

    public Boolean isUbidataAdmin() {
        return (Boolean) jsonObject.get("ubidataAdmin");
    }

    public void isUbidataAdmin(Boolean flag) {
        jsonObject.put("ubidataAdmin", flag);
    }

    public void setAccountTest(Boolean companyId) {
        jsonObject.put("companyTest", BooleanUtils.toBoolean(companyId));
    }

    public List<String> getPermission() {
        return (List<String>) jsonObject.get("permission");
    }

    public void setPermission(List<String> permission) {
        jsonObject.put("permission", permission);
    }

    public List<Role> getRoles() {
        return (List<Role>) jsonObject.get("roles");
    }

    public void setRoles(List<Role> roles) {
        jsonObject.put("roles", roles);
    }

    public void changePassword(Boolean changePassword) {
        jsonObject.put("changePassword", changePassword);
    }

    public Boolean changePassword() {
        return (Boolean) jsonObject.get("changePassword");
    }

    public void userLinkedAnotherTracker(Boolean userLinkedAnotherTracker) {
        jsonObject.put("userLinkedAnotherTracker", userLinkedAnotherTracker);
    }

    public Boolean userLinkedAnotherTracker() {
        return (Boolean) jsonObject.get("userLinkedAnotherTracker");
    }

    public void trackerLinkedAnotherUser(Boolean trackerLinkedAnotherUser) {
        jsonObject.put("trackerLinkedAnotherUser", trackerLinkedAnotherUser);
    }

    public Boolean trackerLinkedAnotherUser() {
        return (Boolean) jsonObject.get("trackerLinkedAnotherUser");
    }

    public Boolean getIsWithoutExpiration() {
        return (Boolean) jsonObject.get("isWithoutExpiration");
    }

    public void setIsWithoutExpiration(Boolean isWithoutExpiration) {
        jsonObject.put("isWithoutExpiration", isWithoutExpiration);
    }

    public void addAttribute(String name, String value) {
        jsonObject.put(name, value);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet();
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }
}
