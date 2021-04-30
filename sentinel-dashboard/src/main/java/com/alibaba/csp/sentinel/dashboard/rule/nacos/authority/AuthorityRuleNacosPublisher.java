
package com.alibaba.csp.sentinel.dashboard.rule.nacos.authority;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfig;
import com.alibaba.csp.sentinel.dashboard.rule.nacos.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.nacos.api.config.ConfigService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author linjiayong
 * @description 授权规则
 * @date 2021/2/4 14:08
 */
@Component("authorityRuleNacosPublisher")
public class AuthorityRuleNacosPublisher implements DynamicRulePublisher<java.util.List<AuthorityRuleEntity>> {
    @Autowired
    private ConfigService configService;
    @Autowired
    private NacosConfig nacosConfig;
    @Autowired
    private Converter<java.util.List<AuthorityRuleCorrectEntity>, String> converter;

    @Override
    public void publish(String app, java.util.List<AuthorityRuleEntity> rules) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        for (AuthorityRuleEntity rule : rules) {
            if (rule.getApp() == null) {
                rule.setApp(app);
            }
        }
        //  转换
        List<AuthorityRuleCorrectEntity> list = rules.stream().map(rule -> {
            AuthorityRuleCorrectEntity entity = new AuthorityRuleCorrectEntity();
            BeanUtils.copyProperties(rule,entity);
            return entity;
        }).collect(Collectors.toList());

        configService.publishConfig(app + NacosConfigUtil.AUTHORITY_DATA_ID_POSTFIX,
                NacosConfigUtil.GROUP_ID, converter.convert(list));
    }
}