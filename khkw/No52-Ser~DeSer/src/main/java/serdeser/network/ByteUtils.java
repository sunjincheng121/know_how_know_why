package serdeser.network;

/**
 * 项目名称: Apache Flink 知其然，知其所以然 - Ser/DeSer
 * 功能描述: 测试代码，仅限于示例，不能投产
 * 操作步骤:
 * <p>
 * 作者： 孙金城
 * 日期： 2021/06/24
 */
public class ByteUtils {
    public static byte[] append(byte[] src, byte[] target, int pos) {
        if (src.length + pos >= target.length) {
            byte[] ext = new byte[target.length * 2]; // src.length << target.length
            System.arraycopy(target, 0, ext, 0, target.length);
            target = ext;
            System.out.println(String.format("Expand capacity to [%d] ", target.length));
        }
        System.arraycopy(src, 0, target, pos, src.length);
        return target;
    }

    public static byte[] intToBytes(int i) {
        return new byte[]{
                (byte) ((i >> 24) & 0xFF),
                (byte) ((i >> 16) & 0xFF),
                (byte) ((i >> 8) & 0xFF),
                (byte) (i & 0xFF)
        };
    }

    public static int bytesToInt(byte[] bytes) {
        // compatible to long

        int length = bytes.length;
        long r = 0;
        for (int i = 0; i < length; i++) {
            r += ((bytes[length - 1 - i] & 0xFF) << (i * 8));
        }

        if (r > Integer.MAX_VALUE) {
            throw new RuntimeException("Row count is larger than Integer.MAX_VALUE");
        }

        return (int) r;
    }

    public static byte[] stringToBytes(String str) {
        return str.getBytes();
    }

    public static String bytesToString(byte[] byteStr) {
        return new String(byteStr);
    }
}
