package com.github.niefy.modules.wx.util;

import com.github.niefy.modules.wx.entity.WxAccount;
import com.github.niefy.modules.wx.enums.MpAuthorizeType;
import com.github.niefy.modules.wx.service.WxAccountService;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.open.api.WxOpenComponentService;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * <h2>WxMpServiceUtil</h2>
 *
 * @author: 苍海之南
 * @since: 2020-09-30 10:44
 **/
@Component
@AllArgsConstructor
public class WxMpServiceUtil {

    private final WxMpService wxMpService;

    private final WxOpenComponentService wxOpenComponentService;

    private final WxAccountService wxAccountService;

    public WxMpService switchoverTo(String appid) {
        final WxAccount wxAccount = wxAccountService.getById(appid);
        if (Objects.isNull(wxAccount)) {
            throw new RuntimeException("公众号[" + appid + "]数据不存在，请核实");
        }

        if (wxAccount.getAuthorizeType().equals(MpAuthorizeType.OPEN.name())){
            return wxOpenComponentService.getWxMpServiceByAppid(appid);
        }else {
            return wxMpService.switchoverTo(appid);
        }
    }

}
