import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        BTClient btClient = new BTClient();

        btClient.searchDevices(false); // sync search
        ArrayList<BTClient.DeviceInfo> deviceInfoArrayList = btClient.deviceList();
        for (BTClient.DeviceInfo deviceInfo : deviceInfoArrayList) {
            System.out.println(" Device: " + deviceInfo.name + " (" + deviceInfo.address + ")");
        }

        String address = "64B8531504A4"; // Me
        //String address = "BC765EA6CA32"; // Sobol
        boolean connectTo = btClient.connectTo(address);
        if (!connectTo) { System.exit(1); }
        System.out.println(" My address: " + btClient.localAddress());

        String KEY = "[leha's_super_key]";
        btClient.sendS("pubkey:" + KEY);
        String s = btClient.receiveS();
        if (s.equals("bad_request")) { System.exit(1); }

        btClient.disconnect();
        btClient.connectTo(address);

        String SERVER_MSG_TO_SIGN = "[from_server]";
        btClient.sendS("request:keklol:" + SERVER_MSG_TO_SIGN);
        s = btClient.receiveS();
        if (s.equals("bad_request")) { System.exit(1); }
        String[] ss = s.split(":");
        btClient.sendS("signed:" + ss[1] + KEY);
        s = btClient.receiveS();
        if (s.equals("bad_request")) { System.exit(1); }

        btClient.disconnect();
    }
}
