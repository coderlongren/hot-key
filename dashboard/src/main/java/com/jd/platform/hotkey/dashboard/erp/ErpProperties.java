package com.jd.platform.hotkey.dashboard.erp;

import com.jd.platform.hotkey.dashboard.autoconfigure.AbstractProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * User:  fuxueliang
 * Date:  16/8/17
 * Email: fuxueliang@jd.com
 */
@Data
@Component
@ConfigurationProperties(prefix = "erp")
public class ErpProperties extends AbstractProperties {

    private String excludePath;

    private String loginUrl;

    private String ssoAppUrl;

    private String ssoAppKey;

    private String ssoAppToken;

    private Boolean enabled;

}
