import com.jd.platform.hotkey.common.rule.KeyRule;

/**
 * @author wuweifeng wrote on 2020-04-02
 * @version 1.0
 */
public class KeyRuleTool {
    public static void main(String[] args) {
        KeyRule keyRule = new KeyRule.Builder().key("*").prefix(false).duration(60).interval(5).threshold(100).build();
    }
}
