package ch.swissbytes.domain.dto;

import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.app.user.model.User;
import ch.swissbytes.module.shared.utils.EntityUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
public class AccountDto implements Serializable {

    private Long id;
    private String nameAccount;
    private String address;
    private String phoneNumber;
    private String email;
    private Integer line;
    private String documentNumber;
    private String socialReason;
    private String nit;
    private Boolean status;
    private String statusName;
    private UserManagementDto user;

    public static AccountDto createNew() {
        AccountDto accountDto = new AccountDto();
        accountDto.id = EntityUtil.DEFAULT_LONG;
        accountDto.nameAccount = EntityUtil.DEFAULT_STRING;
        accountDto.address = EntityUtil.DEFAULT_STRING;
        accountDto.phoneNumber = EntityUtil.DEFAULT_STRING;
        accountDto.email = EntityUtil.DEFAULT_STRING;
        accountDto.line = EntityUtil.DEFAULT_INTEGER;
        accountDto.documentNumber = EntityUtil.DEFAULT_STRING;
        accountDto.nit = EntityUtil.DEFAULT_STRING;
        accountDto.socialReason = EntityUtil.DEFAULT_STRING;
        accountDto.status = EntityUtil.DEFAULT_BOOLEAN;
        accountDto.statusName = EntityUtil.DEFAULT_STRING;
        return accountDto;
    }

    public Account fromDto() {
        Account account = Account.createNew();
        account.setName(getNameAccount());
        account.setAddress(getAddress());
        account.setPhoneNumber(getPhoneNumber());
        account.setEmail(getEmail());
        account.setLine(getLine());
        account.setDocumentNumber(getDocumentNumber());
        account.setSocialReason(getSocialReason());
        account.setNit(getNit());
        account.setStatus(getStatus() ? StatusEnum.ENABLED : StatusEnum.DISABLED);
        return account;
    }

    public static AccountDto fromAccountEntity(Account account) {
        AccountDto accountDto = createNew();
        accountDto.id = account.getId();
        accountDto.nameAccount = account.getName();
        accountDto.address = account.getAddress();
        accountDto.phoneNumber = account.getPhoneNumber();
        accountDto.email = account.getEmail();
        accountDto.line = account.getLine();
        accountDto.documentNumber = account.getDocumentNumber();
        accountDto.socialReason = account.getSocialReason();
        accountDto.nit = account.getNit();
        accountDto.status = account.getStatus().equals(StatusEnum.ENABLED);
        accountDto.statusName = accountDto.getStatusName().equals(StatusEnum.ENABLED) ? "Habilitado" : "Inactivo";
        return accountDto;
    }

    public static List<AccountDto> from(List<Account> accountList) {
        return accountList.stream().map(AccountDto::fromAccountEntity).collect(Collectors.toList());
    }

    public static Account fromAccountDto(AccountDto accountDto) {
        Account account = Account.createNew();
        account.setId(accountDto.getId());
        account.setName(accountDto.getNameAccount());
        account.setAddress(accountDto.getAddress());
        account.setPhoneNumber(accountDto.getPhoneNumber());
        account.setEmail(accountDto.getEmail());
        account.setLine(accountDto.getLine());
        account.setDocumentNumber(accountDto.getDocumentNumber());
        account.setSocialReason(accountDto.getSocialReason());
        account.setNit(accountDto.getNit());
        account.setStatus(accountDto.getStatus() ? StatusEnum.ENABLED : StatusEnum.DISABLED);
        return account;
    }

    public static User from(AccountDto accountDto) {
        User user = User.createNew();
        user.setId(accountDto.getUser().getId());
        user.setUsername(accountDto.getUser().getUsername());
        user.setPassword(accountDto.getUser().getPassword());
        user.setAccount(new Account(accountDto.getId()));
        user.setName(accountDto.getNameAccount());
        user.setPhoneNumber(accountDto.getPhoneNumber());
        user.setEmail(accountDto.getEmail());
        return user;
    }

    public static User from(UserManagementDto dto, User user) {
        user.setUsername(dto.getUsername());
        return user;
    }

    public static List<AccountDto> convertFromUserList(List<User> userList) {
        return userList.stream().map(AccountDto::setCompanyDtoFromUser).collect(Collectors.toList());
    }

    private static UserManagementDto setUserManagementDtoFromUser(User user) {
        UserManagementDto userManagementDto = UserManagementDto.createNew();
        userManagementDto.setId(user.getId());
        userManagementDto.setName(user.getName());
        userManagementDto.setUsername(user.getUsername());
        userManagementDto.setEmail(user.getEmail());
        userManagementDto.setPhoneNumber(user.getPhoneNumber());
        return userManagementDto;
    }

    private static AccountDto setCompanyDtoFromUser(User user) {
        AccountDto accountDto = createNew();
        accountDto.setId(user.getAccount().getId());
        accountDto.setNameAccount(user.getAccount().getName());
        accountDto.setAddress(user.getAccount().getAddress());
        accountDto.setPhoneNumber(user.getAccount().getPhoneNumber());
        accountDto.setEmail(user.getAccount().getEmail());
        accountDto.setStatus(StatusEnum.ENABLED.equals(user.getAccount().getStatus()));
        accountDto.setStatusName(StatusEnum.ENABLED.equals(user.getAccount().getStatus()) ? "Habilitado" : "Inactivo");
        accountDto.setUser(setUserManagementDtoFromUser(user));
        return accountDto;
    }

    public static Account accountBill(AccountDto dto, Account account) {
        account.setDocumentNumber(dto.getDocumentNumber());
        account.setSocialReason(dto.getSocialReason() == null || dto.getSocialReason().isEmpty() ? "S/N" : account.getSocialReason());
        account.setNit(dto.getNit() == null || dto.getNit().isEmpty() ? "0" : account.getNit());
        account.setPhoneNumber(dto.getPhoneNumber());
        return account;
    }

    public static Account from(AccountDto dto, Account account) {
        account.setName(dto.getNameAccount());
        account.setAddress(dto.getAddress());
        account.setPhoneNumber(dto.getEmail());
        return account;
    }
}
