package demo;

import java.io.Serializable;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - demo
 * <p>
 * 作者： 孙金城
 * 日期： 2020/8/13
 */
public class OrderMessage implements Serializable {
    private static final long serialVersionUID = -4082657304129211564L;
    private long orderId; //订单ID
    private String buyerId; //买家ID
    private String productId; //商品ID

    private String salerId; //卖家ID
    private long createTime; //13位数数，毫秒级时间戳，订单创建时间
    private double totalPrice;

    //Kryo默认需要无参数构造函数
    private OrderMessage() {

    }

    public static OrderMessage createTbaoMessage() {
        OrderMessage msg =  new OrderMessage();
        msg.orderId = TableItemFactory.createOrderId();
        msg.buyerId = TableItemFactory.createBuyerId();
        msg.productId = TableItemFactory.createProductId();
        msg.salerId = TableItemFactory.createTbaoSalerId();
        msg.totalPrice = TableItemFactory.createTotalPrice();
        return msg;
    }

    public static OrderMessage createTmallMessage() {
        OrderMessage msg =  new OrderMessage();
        msg.orderId = TableItemFactory.createOrderId();
        msg.buyerId = TableItemFactory.createBuyerId();
        msg.productId = TableItemFactory.createProductId();
        msg.salerId = TableItemFactory.createTmallSalerId();
        msg.totalPrice = TableItemFactory.createTotalPrice();

        return msg;
    }

    @Override
    public String toString() {
        return "OrderMessage{" +
                "orderId=" + orderId +
                ", buyerId='" + buyerId + '\'' +
                ", productId='" + productId + '\'' +
                ", salerId='" + salerId + '\'' +
                ", createTime=" + createTime +
                ", totalPrice=" + totalPrice +
                '}';
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }


    public String getSalerId() {
        return salerId;
    }

    public void setSalerId(String salerId) {
        this.salerId = salerId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

}
