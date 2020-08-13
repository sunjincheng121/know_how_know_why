package demo;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - demo
 * <p>
 * 作者： 孙金城
 * 日期： 2020/8/13
 */
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Random;

public class PaymentMessage implements Serializable{

    private static final long serialVersionUID = -4721410670774102273L;

    private long orderId; //订单ID

    private double payAmount; //金额

    /**
     * 省份: "北京", "上海", "杭州", "深圳", "江西", "重庆", "西藏"
     */
    private short provinceId; //来源

    /**
     * 支付平台
     * 0，无线
     * 1，pc
     */
    private short payPlatform; //支付平台

    /**
     * 付款记录创建时间
     */
    private String createTime; //13位数，毫秒级时间戳

    private static int[] factor = {10, 9, 6, 8, 5, 6, 3}; // total: 47

    private static String[] provinces = {"北京", "上海", "杭州", "深圳", "江西", "重庆", "西藏"};

    //Kryo默认需要无参数构造函数
    public PaymentMessage() {
    }

    private static Random rand = new Random();

    public static PaymentMessage createPaymentMsg(OrderMessage orderMessage) {
        PaymentMessage msg = new PaymentMessage();
        msg.orderId = orderMessage.getOrderId();
        msg.provinceId = generateProvince();
        msg.payPlatform = (short) rand.nextInt(10);
        msg.payPlatform /= 9; // make 90% payment from mobile
        msg.createTime = String.valueOf(new Timestamp(orderMessage.getCreateTime() + rand.nextInt(100)));
        msg.payAmount = orderMessage.getTotalPrice();
        if (rand.nextDouble() > 0.9) msg.payAmount *= 5;
        return msg;
    }

    public static short generateProvince() {
        // 北京：10，上海：9，杭州：6，深圳：8，江西：5，重庆：6，西藏：3。

        // 产生动态分布
        int a = rand.nextInt(7);
        int b = rand.nextInt(7);
        int tmp = factor[a];
        factor[a] = factor[b];
        factor[b] = tmp;

        int v = rand.nextInt(47);
        int sum = 0;
        for (int i = 0; i < factor.length; ++i) {
            sum += factor[i];
            if (sum >= v) {
                return (short) i;
            }
        }
        return 6;
    }

    @Override
    public String toString() {
        return "PaymentMessage{" +
                "orderId=" + orderId +
                ", payAmount=" + payAmount +
                ", provinceId=" + provinceId +
                ", payPlatform=" + payPlatform +
                ", createTime=" + createTime +
                '}';
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(double payAmount) {
        this.payAmount = payAmount;
    }

    public short getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(short provinceId) {
        this.provinceId = provinceId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public short getPayPlatform() {
        return payPlatform;
    }
}
