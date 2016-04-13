package kerecs.bluetoothsignal;

/**
 * Created by LiuJun on 2016/4/4.
 */
public class Device {
    private String name;
    private String mac;
    private float rssi;

    public Device(String name, String mac, float rssi) {
        this.name = name;
        this.mac = mac;
        this.rssi = rssi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public float getRssi() {
        return rssi;
    }

    public void setRssi(float rssi) {
        this.rssi = rssi;
    }

    //d=10^((ABS(RSSI)-A)/(10*n))、A 代表在距离一米时的信号强度(45 ~ 49), n 代表环境对信号的衰减系数(3.25 ~ 4.5)
    public float getDistance() {
        return (float) Math.pow(10, (Math.abs(rssi) - 45) / (10 * 3.25));
    }
}
