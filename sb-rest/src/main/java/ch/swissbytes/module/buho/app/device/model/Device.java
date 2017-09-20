package ch.swissbytes.module.buho.app.device.model;

import ch.swissbytes.domain.entities.DeviceType;
import ch.swissbytes.domain.enumerator.StatusEnum;
import ch.swissbytes.module.buho.app.account.model.Account;
import ch.swissbytes.module.buho.service.configuration.ConfigurationKey;
import ch.swissbytes.module.buho.service.configuration.KeyAppConfiguration;
import ch.swissbytes.module.shared.utils.EntityUtil;
import ch.swissbytes.module.shared.utils.LongUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.Random;

@Entity
@Table(name = "device", uniqueConstraints = {@UniqueConstraint(name = "idx_device_imei", columnNames = {"imei"})})
@Getter
@Setter
@ToString
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "imei")
    private String imei;

    @Column(name = "name")
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "icon")
    private String icon;

    @Column(name = "color")
    private String color;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "sos_mails")
    private String sosMails;

    @Column(name = "sos_cellphones")
    private String sosCellphones;

    @Column(name = "battery_mails")
    private String batteryMails;

    @Column(name = "battery_cellphones")
    private String batteryCellphones;

    @Column(name = "mail_drop_alarm")
    private String mailsDropAlarm;

    @Column(name = "cellphone_drop_alarm")
    private String cellphoneDropAlarm;

    @Column(name = "gcm_token")
    private String gcmToken;

    @Column(name = "modification_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date modificationDate;

    @Column(name = "generated_id")
    private String generatedId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", foreignKey = @ForeignKey(name = "fk_device_account_id"))
    private Account account;

    @ManyToOne
    @JoinColumn(name = "device_type_id", foreignKey = @ForeignKey(name = "fk_device_device_type_id"))
    private DeviceType deviceType;

    public Device(Long id) {
        this.id = id;
    }

    public Device() {
    }

    public static Device createNew() {
        Device device = new Device();
        device.imei = EntityUtil.DEFAULT_STRING;
        device.name = EntityUtil.DEFAULT_STRING;
        device.icon = getRandomIcon();
        device.color = getRandomColor();
        device.account = null;
        device.deviceType = null;
        device.generatedId = EntityUtil.DEFAULT_STRING;
        device.phoneNumber = EntityUtil.DEFAULT_STRING;
        device.status = StatusEnum.ENABLED;
        device.sosMails = EntityUtil.DEFAULT_STRING;
        device.sosCellphones = EntityUtil.DEFAULT_STRING;
        device.batteryMails = EntityUtil.DEFAULT_STRING;
        device.batteryCellphones = EntityUtil.DEFAULT_STRING;
        device.gcmToken = EntityUtil.DEFAULT_STRING;
        device.modificationDate = EntityUtil.DEFAULT_DATE;
        return device;
    }

    public static Device createSmarthPhone(String imei, String gcmToken) {
        Device device = createNew();
        device.imei = imei;
        device.gcmToken = gcmToken;
        device.icon = getRandomIcon();
        device.color = getRandomColor();
        device.deviceType = new DeviceType(KeyAppConfiguration.getLong(ConfigurationKey.SMARTPHONE_TYPE_ID));
        return device;
    }

    @Transient
    public boolean isNew() {
        return LongUtil.isEmpty(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Device)) return false;

        Device other = (Device) obj;
        return this.id == other.id;
    }

    private static String getRandomColor() {
        String color = "";
        Random random = new Random();
        String[] colors = {"a", "b", "c", "d", "f"};
        for (int i = 0; i < 3; i++) {
            color += random.nextInt(9) + colors[random.nextInt(colors.length)];
        }
        return color;
    }

    private static String getRandomIcon() {
        String[] fontAwesome = {"fa-music", "fa-search", "fa-envelope-o", "fa-heart"
                , "fa-star"
                , "fa-star-o"
                , "fa-user"
                , "fa-film"
                , "fa-th-large"
                , "fa-th"
                , "fa-th-list"
                , "fa-check"
                , "fa-search-plus"
                , "fa-search-minus"
                , "fa-power-off"
                , "fa-signal"
                , "fa-cog"
                , "fa-trash-o"
                , "fa-home"
                , "fa-file-o"
                , "fa-clock-o"
                , "fa-road"
                , "fa-download"
                , "fa-arrow-circle-o-down"
                , "fa-arrow-circle-o-up"
                , "fa-inbox"
                , "fa-play-circle-o"
                , "fa-repeat"
                , "fa-refresh"
                , "fa-list-alt"
                , "fa-lock"
                , "fa-flag"
                , "fa-headphones"
                , "fa-volume-off"
                , "fa-volume-down"
                , "fa-volume-up"
                , "fa-qrcode"
                , "fa-barcode"
                , "fa-tag"
                , "fa-tags"
                , "fa-book"
                , "fa-bookmark"
                , "fa-print"
                , "fa-camera"
                , "fa-font"
                , "fa-bold"
                , "fa-italic"
                , "fa-text-height"
                , "fa-text-width"
                , "fa-align-left"
                , "fa-align-center"
                , "fa-align-right"
                , "fa-align-justify"
                , "fa-list"
                , "fa-outdent"
                , "fa-indent"
                , "fa-video-camera"
                , "fa-picture-o"
                , "fa-pencil"
                , "fa-map-marker"
                , "fa-adjust"
                , "fa-tint"
                , "fa-pencil-square-o"
                , "fa-share-square-o"
                , "fa-check-square-o"
                , "fa-arrows"
                , "fa-step-backward"
                , "fa-fast-backward"
                , "fa-backward"
                , "fa-play"
                , "fa-pause"
                , "fa-stop"
                , "fa-forward"
                , "fa-fast-forward"
                , "fa-step-forward"
                , "fa-eject"
                , "fa-chevron-left"
                , "fa-chevron-right"
                , "fa-plus-circle"
                , "fa-minus-circle"
                , "fa-times-circle"
                , "fa-check-circle"
                , "fa-question-circle"
                , "fa-info-circle"
                , "fa-crosshairs"
                , "fa-times-circle-o"
                , "fa-check-circle-o"
                , "fa-ban"
                , "fa-arrow-left"
                , "fa-arrow-right"
                , "fa-arrow-up"
                , "fa-arrow-down"
                , "fa-share"
                , "fa-expand"
                , "fa-compress"
                , "fa-plus"
                , "fa-minus"
                , "fa-asterisk"
                , "fa-exclamation-circle"
                , "fa-gift"
                , "fa-leaf"
                , "fa-fire"
                , "fa-eye"
                , "fa-eye-slash"
                , "fa-exclamation-triangle"
                , "fa-plane"
                , "fa-calendar"
                , "fa-random"
                , "fa-comment"
                , "fa-magnet"
                , "fa-chevron-up"
                , "fa-chevron-down"
                , "fa-retweet"
                , "fa-shopping-cart"
                , "fa-folder"
                , "fa-folder-open"
                , "fa-arrows-v"
                , "fa-arrows-h"
                , "fa-bar-chart-o"
                , "fa-twitter-square"
                , "fa-facebook-square"
                , "fa-camera-retro"
                , "fa-key"
                , "fa-cogs"
                , "fa-comments"
                , "fa-thumbs-o-up"
                , "fa-star-half"
                , "fa-heart-o"
                , "fa-sign-out"
                , "fa-linkedin-square"
                , "fa-thumb-tack"
                , "fa-external-link"
                , "fa-sign-in"
                , "fa-trophy"
                , "fa-github-square"
                , "fa-upload"
                , "fa-lemon-o"
                , "fa-phone"
                , "fa-square-o"
                , "fa-bookmark-o"
                , "fa-phone-square"
                , "fa-twitter"
                , "fa-facebook"
                , "fa-github"
                , "fa-unlock"
                , "fa-credit-card"
                , "fa-rss"
                , "fa-hdd-o"
                , "fa-bullhorn"
                , "fa-bell"
                , "fa-certificate"
                , "fa-hand-o-right"
                , "fa-hand-o-left"
                , "fa-hand-o-up"
                , "fa-hand-o-down"
                , "fa-arrow-circle-left"
                , "fa-arrow-circle-right"
                , "fa-arrow-circle-up"
                , "fa-arrow-circle-down"
                , "fa-globe"
                , "fa-wrench"
                , "fa-tasks"
                , "fa-filter"
                , "fa-briefcase"
                , "fa-arrows-alt"
                , "fa-users"
                , "fa-link"
                , "fa-cloud"
                , "fa-flask"
                , "fa-scissors"
                , "fa-files-o"
                , "fa-paperclip"
                , "fa-floppy-o"
                , "fa-square"
                , "fa-bars"
                , "fa-list-ul"
                , "fa-list-ol"
                , "fa-strikethrough"
                , "fa-underline"
                , "fa-table"
                , "fa-magic"
                , "fa-truck"
                , "fa-pinterest"
                , "fa-pinterest-square"
                , "fa-google-plus-square"
                , "fa-google-plus"
                , "fa-money"
                , "fa-caret-down"
                , "fa-caret-up"
                , "fa-caret-left"
                , "fa-caret-right"
                , "fa-columns"
                , "fa-sort"
                , "fa-sort-desc"
                , "fa-sort-asc"
                , "fa-envelope"
                , "fa-linkedin"
                , "fa-undo"
                , "fa-gavel"
                , "fa-tachometer"
                , "fa-comment-o"
                , "fa-comments-o"
                , "fa-bolt"
                , "fa-sitemap"
                , "fa-umbrella"
                , "fa-clipboard"
                , "fa-lightbulb-o"
                , "fa-exchange"
                , "fa-cloud-download"
                , "fa-cloud-upload"
                , "fa-user-md"
                , "fa-stethoscope"
                , "fa-suitcase"
                , "fa-bell-o"
                , "fa-coffee"
                , "fa-cutlery"
                , "classname"
                , "fa-file-text-o"
                , "fa-building-o"
                , "fa-hospital-o"
                , "fa-ambulance"
                , "fa-medkit"
                , "fa-fighter-jet"
                , "fa-beer"
                , "fa-h-square"
                , "fa-plus-square"
                , "fa-angle-double-left"
                , "fa-angle-double-right"
                , "fa-angle-double-up"
                , "fa-angle-double-down"
                , "fa-angle-left"
                , "fa-angle-right"
                , "fa-angle-up"
                , "fa-angle-down"
                , "fa-desktop"
                , "fa-laptop"
                , "fa-tablet"
                , "fa-mobile"
                , "fa-circle-o"
                , "fa-quote-left"
                , "fa-quote-right"
                , "fa-spinner"
                , "fa-circle"
                , "fa-reply"
                , "fa-github-alt"
                , "fa-folder-o"
                , "fa-folder-open-o"
                , "fa-smile-o"
                , "fa-frown-o"
                , "fa-meh-o"
                , "fa-gamepad"
                , "fa-keyboard-o"
                , "fa-flag-o"
                , "fa-flag-checkered"
                , "fa-terminal"
                , "fa-code"
                , "fa-reply-all"
                , "fa-star-half-o"
                , "fa-location-arrow"
                , "fa-crop"
                , "fa-code-fork"
                , "fa-chain-broken"
                , "fa-question"
                , "fa-info"
                , "fa-exclamation"
                , "fa-superscript"
                , "fa-subscript"
                , "fa-eraser"
                , "fa-puzzle-piece"
                , "fa-microphone"
                , "fa-microphone-slash"
                , "fa-shield"
                , "fa-calendar-o"
                , "fa-fire-extinguisher"
                , "fa-rocket"
                , "fa-maxcdn"
                , "fa-chevron-circle-left"
                , "fa-chevron-circle-right"
                , "fa-chevron-circle-up"
                , "fa-chevron-circle-down"
                , "fa-html5"
                , "fa-css3"
                , "fa-anchor"
                , "fa-unlock-alt"
                , "fa-bullseye"
                , "fa-ellipsis-h"
                , "fa-ellipsis-v"
                , "fa-rss-square"
                , "fa-play-circle"
                , "fa-ticket"
                , "fa-minus-square"
                , "fa-minus-square-o"
                , "fa-level-up"
                , "fa-level-down"
                , "fa-check-square"
                , "fa-pencil-square"
                , "fa-external-link-square"
                , "fa-share-square"
                , "fa-compass"
                , "fa-caret-square-o-down"
                , "fa-caret-square-o-up"
                , "fa-caret-square-o-right"
                , "fa-eur"
                , "fa-gbp"
                , "fa-usd"
                , "fa-inr"
                , "fa-jpy"
                , "fa-rub"
                , "fa-krw"
                , "fa-btc"
                , "fa-file"
                , "fa-file-text"
                , "fa-sort-alpha-asc"
                , "fa-sort-alpha-desc"
                , "fa-sort-amount-asc"
                , "fa-sort-amount-desc"
                , "fa-sort-numeric-asc"
                , "fa-sort-numeric-desc"
                , "fa-thumbs-up"
                , "fa-thumbs-down"
                , "fa-youtube-square"
                , "fa-youtube"
                , "fa-xing"
                , "fa-xing-square"
                , "fa-youtube-play"
                , "fa-dropbox"
                , "fa-stack-overflow"
                , "fa-instagram"
                , "fa-flickr"
                , "fa-adn"
                , "fa-bitbucket"
                , "fa-bitbucket-square"
                , "fa-tumblr"
                , "fa-tumblr-square"
                , "fa-long-arrow-down"
                , "fa-long-arrow-up"
                , "fa-long-arrow-left"
                , "fa-long-arrow-right"
                , "fa-apple"
                , "fa-windows"
                , "fa-android"
                , "fa-linux"
                , "fa-dribbble"
                , "fa-skype"
                , "fa-foursquare"
                , "fa-trello"
                , "fa-female"
                , "fa-male"
                , "fa-gittip"
                , "fa-sun-o"
                , "fa-moon-o"
                , "fa-archive"
                , "fa-bug"
                , "fa-vk"
                , "fa-weibo"
                , "fa-renren"
                , "fa-pagelines"
                , "fa-stack-exchange"
                , "fa-arrow-circle-o-right"
                , "fa-arrow-circle-o-left"
                , "fa-caret-square-o-left"
                , "fa-dot-circle-o"
                , "fa-wheelchair"
                , "fa-vimeo-square"
                , "fa-try"
                , "fa-plus-square-o"
                , "fa-space-shuttle"
                , "fa-slack"
                , "fa-envelope-square"
                , "fa-wordpress"
                , "fa-openid"
                , "fa-university"
                , "fa-graduation-cap"
                , "fa-yahoo"
                , "fa-google"
                , "fa-reddit"
                , "fa-reddit-square"
                , "fa-stumbleupon-circle"
                , "fa-stumbleupon"
                , "fa-delicious"
                , "fa-digg"
                , "fa-pied-piper"
                , "fa-pied-piper-alt"
                , "fa-drupal"
                , "fa-joomla"
                , "fa-language"
                , "fa-fax"
                , "fa-building"
                , "fa-child"
                , "fa-paw"
                , "fa-spoon"
                , "fa-cube"
                , "fa-cubes"
                , "fa-behance"
                , "fa-behance-square"
                , "fa-steam"
                , "fa-steam-square"
                , "fa-recycle"
                , "fa-car"
                , "fa-taxi"
                , "fa-tree"
                , "fa-spotify"
                , "fa-deviantart"
                , "fa-soundcloud"
                , "fa-database"
                , "fa-file-pdf-o"
                , "fa-file-word-o"
                , "fa-file-excel-o"
                , "fa-file-powerpoint-o"
                , "fa-file-image-o"
                , "fa-file-archive-o"
                , "fa-file-audio-o"
                , "fa-file-video-o"
                , "fa-file-code-o"
                , "fa-vine"
                , "fa-codepen"
                , "fa-jsfiddle"
                , "fa-life-ring"
                , "fa-circle-o-notch"
                , "fa-rebel"
                , "fa-empire"
                , "fa-git-square"
                , "fa-git"
                , "fa-hacker-news"
                , "fa-tencent-weibo"
                , "fa-qq"
                , "fa-weixin"
                , "fa-paper-plane"
                , "fa-paper-plane-o"
                , "fa-history"
                , "fa-circle-thin"
                , "fa-header"
                , "fa-paragraph"
                , "fa-sliders"
                , "fa-share-alt"
                , "fa-share-alt-square"
                , "fa-bomb"};

        Random rd = new Random();
        return fontAwesome[rd.nextInt(fontAwesome.length)];
    }
}
