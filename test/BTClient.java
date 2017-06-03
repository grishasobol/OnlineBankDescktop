import javax.bluetooth.*;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.*;
import java.util.*;

/** ---- Documentation ----
 *
 * 1) class DeviceInfo { // info about BT device
 *     String name, address; // BT name and address
 *     DeviceInfo(String name, String address);
 * }
 *
 * 2) class StopPoint { // semaphore
 *     activate(); // wait till somepony makes "deactivate()"
 *     deactivate(); // release
 * }
 *
 *
 * 1) BTClient(); // init the class
 *
 * 2) boolean connectTo(String address); // connect to BT device; returns succeed
 *
 * 3) ArrayList<DeviceInfo> deviceList(); // get list of founded devices
 *
 * 4) disconnect(); // end last connection
 *
 * 5) String localAddress(); // BT address of my device
 *
 * 6) String localName(); // BT name of my device
 *
 * 7) String receive(); // receive data from connected device; non-'null' if all ok
 *
 * 8) String receiveS(); // receive data from connected device and notify that we got it; non-'null' if all ok
 *
 * 9) StopPoint searchDevices(boolean async); // search for BT devices (and save the list)
 *    # if "async" 'true': returns semaphore, which'll be released in the end of searching; if "async"
 *    # is 'false' : the function waits by itself
 *
 * 10) boolean send(String string); // send data to connected device; 'true' if all ok
 *
 * 11) boolean sendS(String string); // send data to connected device and check that it got it; 'true' if all ok
 *
 */

public class BTClient {
    private class BTSearcher implements DiscoveryListener {
        private int devices;

        public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
            _DBG_("deviceDiscovered");
            ++devices;
            try {
                discoveryAgent.searchServices(null, uuids, remoteDevice, this);
            } catch (Exception e) { e.printStackTrace(); }
        }

        public void inquiryCompleted(int i) {
            _DBG_("inquiryCompleted");
            if (devices == 0) {
                stopPoint.deactivate();
            }
        }

        public void servicesDiscovered(int i, ServiceRecord[] serviceRecords) {
            _DBG_("servicesDiscovered");

            if ((serviceRecords != null) && (serviceRecords.length > 0)) {
                String url = serviceRecords[0].getConnectionURL(ServiceRecord.AUTHENTICATE_ENCRYPT, false);
                if (url == null) {
                    _DBG_("device doesn't support service");
                    return;
                }
                _DBG_("device supports service");

                RemoteDevice remoteDevice = serviceRecords[0].getHostDevice();
                String name = null;
                try {
                    name = remoteDevice.getFriendlyName(ASK_NAME);
                } catch (Exception e) { e.printStackTrace(); }
                String address = remoteDevice.getBluetoothAddress();
                _DBG_("device: " + name + " " + address);

                DeviceInfo deviceInfo = new DeviceInfo(name, address);
                Device device = new Device(remoteDevice, deviceInfo, url);
                deviceArrayList.add(device);
            }
            else {
                _DBG_("device doesn't support service");
            }
        }

        public void serviceSearchCompleted(int i, int i1) {
            _DBG_("serviceSearchCompleted");
            --devices;
            if (devices == 0) {
                stopPoint.deactivate();
            }
        }
    }

    private class Device {
        public RemoteDevice rdev;
        public DeviceInfo info;
        public String url;

        Device(RemoteDevice rdev_, DeviceInfo info_, String url_) {
            rdev = rdev_;
            info = info_;
            url = url_;
        }
    }

    public class DeviceInfo {
        public String name;
        public String address;

        DeviceInfo(String name_, String address_) {
            name = name_;
            address = address_;
        }
    }

    public class StopPoint {
        public void activate() {
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        public void deactivate() {
            synchronized (this) {
                this.notify();
            }
        }
    }


    private static final boolean ASK_NAME = false; // if 'true' - requests BT device name too long
    private static final boolean DEBUG = true;
    private static final String UUID_VAL = "446118f08b1e11e29e960800200c9a66";

    private final StopPoint stopPoint = new StopPoint();

    private ArrayList<Device> deviceArrayList;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private DiscoveryAgent discoveryAgent;
    private LocalDevice localDevice;
    private StreamConnection streamConnection;
    private UUID[] uuids;


    private void _DBG_(String msg) {
        if (DEBUG) {
            System.out.println("\t[DBG] : [ " + msg + " ]");
        }
    }

    BTClient() {
        try {
            localDevice = LocalDevice.getLocalDevice();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public boolean connectTo(String address) {
        // check if already connected
        if (streamConnection != null) {
            _DBG_("already connected");
            return false;
        }

        // find this device
        RemoteDevice remoteDevice = null;
        String url = null;
        if (deviceArrayList != null) {
            for (Device device : deviceArrayList) {
                String saved_device_addr = device.info.address;
                if (saved_device_addr.equals(address)) {
                    _DBG_("found saved device");
                    remoteDevice = device.rdev;
                    url = device.url;
                    break;
                }
            }
        }
        if (remoteDevice == null) {
            _DBG_("no such saved device");
            return false;
        }

        // connect
        _DBG_("try to connect");
        try {
            streamConnection = (StreamConnection)Connector.open(url);
        } catch (Exception e) { e.printStackTrace(); }
        _DBG_("connected");

        return true;
    }

    public ArrayList<DeviceInfo> deviceList() {
        ArrayList<DeviceInfo> ret = new ArrayList<DeviceInfo>();
        if (deviceArrayList != null) {
            for (Device device : deviceArrayList) {
                ret.add(device.info);
            }
        }
        return ret;
    }

    public void disconnect() {
        if (streamConnection == null) {
            _DBG_("disconnected already");
            return;
        }

        if (dataOutputStream != null) {
            _DBG_("close output stream");
            try {
                dataOutputStream.close();
            } catch (Exception e) { e.printStackTrace(); }
            dataOutputStream = null;
        }

        if (dataInputStream != null) {
            _DBG_("close input stream");
            try {
                dataInputStream.close();
            } catch (Exception e) { e.printStackTrace(); }
            dataInputStream = null;
        }

        _DBG_("disconnect");
        try {
            streamConnection.close();
        } catch (Exception e) { e.printStackTrace(); }
        streamConnection = null;
    }

    public String localAddress() { return localDevice.getBluetoothAddress(); }

    public String localName() { return localDevice.getFriendlyName(); }

    public String receive() {
        if (streamConnection == null) {
            _DBG_("not connected");
            return null;
        }

        if (dataInputStream == null) {
            _DBG_("open input stream");
            try {
                dataInputStream = new DataInputStream(streamConnection.openInputStream());
            } catch (Exception e) { e.printStackTrace(); }
            if (dataInputStream == null) {
                _DBG_("can't open input stream");
                return null;
            }
        }

        _DBG_("receiving");
        String string_to_receive = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
        try {
            string_to_receive = bufferedReader.readLine();
        } catch (Exception e) { e.printStackTrace(); }
        _DBG_("received: \"" + string_to_receive + "\"");

        return string_to_receive;
    }

    public String receiveS() {
        if (streamConnection == null) {
            _DBG_("not connected");
            return null;
        }

        if (dataInputStream == null) {
            _DBG_("open input stream");
            try {
                dataInputStream = new DataInputStream(streamConnection.openInputStream());
            } catch (Exception e) { e.printStackTrace(); }
            if (dataInputStream == null) {
                _DBG_("can't open input stream");
                return null;
            }
        }

        if (dataOutputStream == null) {
            _DBG_("open output stream");
            try {
                dataOutputStream = new DataOutputStream(streamConnection.openOutputStream());
            } catch (Exception e) { e.printStackTrace(); }
            if (dataOutputStream == null) {
                _DBG_("can't open output stream");
                return null;
            }
        }

        _DBG_("S-receiving");
        String data_in = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
        try {
            data_in = bufferedReader.readLine();
        } catch (Exception e) { e.printStackTrace(); }
        _DBG_("answer");
        String data_out = "" + "\n";
        try {
            dataOutputStream.writeBytes(data_out);
            dataOutputStream.flush();
        } catch (Exception e) { e.printStackTrace(); }
        _DBG_("ok receiving: " + data_in + "\"");

        return data_in;
    }

    public StopPoint searchDevices(boolean async) {
        discoveryAgent = localDevice.getDiscoveryAgent();
        deviceArrayList = new ArrayList<Device>();
        BTSearcher btSearcher = new BTSearcher();
        uuids = new UUID[1];
        uuids[0] = new UUID(UUID_VAL, false);

        _DBG_("start searching devices");
        try {
            discoveryAgent.startInquiry(DiscoveryAgent.GIAC, btSearcher);
        } catch (Exception e) { e.printStackTrace(); }

        if (async) {
            // return semaphore
            _DBG_("async searching");
            return stopPoint;
        }
        else {
            // stall
            stopPoint.activate();
            _DBG_("end searching devices");
            return null;
        }
    }

    public boolean send(String string) {
        if (streamConnection == null) {
            _DBG_("not connected");
            return false;
        }

        if (dataOutputStream == null) {
            _DBG_("open output stream");
            try {
                dataOutputStream = new DataOutputStream(streamConnection.openOutputStream());
            } catch (Exception e) { e.printStackTrace(); }
            if (dataOutputStream == null) {
                _DBG_("can't open output stream");
                return false;
            }
        }

        _DBG_("sending: \"" + string + "\"");
        String string_to_send = string + "\n";
        try {
            dataOutputStream.writeBytes(string_to_send);
            dataOutputStream.flush();
        } catch (Exception e) { e.printStackTrace(); }
        _DBG_("sent");

        return true;
    }

    public boolean sendS(String string) {
        if (streamConnection == null) {
            _DBG_("not connected");
            return false;
        }

        if (dataOutputStream == null) {
            _DBG_("open output stream");
            try {
                dataOutputStream = new DataOutputStream(streamConnection.openOutputStream());
            } catch (Exception e) { e.printStackTrace(); }
            if (dataOutputStream == null) {
                _DBG_("can't open output stream");
                return false;
            }
        }

        if (dataInputStream == null) {
            _DBG_("open input stream");
            try {
                dataInputStream = new DataInputStream(streamConnection.openInputStream());
            } catch (Exception e) { e.printStackTrace(); }
            if (dataInputStream == null) {
                _DBG_("can't open input stream");
                return false;
            }
        }

        _DBG_("S-sending: \"" + string + "\"");
        String data_out = string + "\n";
        try {
            dataOutputStream.writeBytes(data_out);
            dataOutputStream.flush();
        } catch (Exception e) { e.printStackTrace(); }
        _DBG_("answer");
        String data_in = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
        try {
            data_in = bufferedReader.readLine();
        } catch (Exception e) { e.printStackTrace(); }
        if (!data_in.equals("")) {
            _DBG_("error sending");
            return false;
        }
        _DBG_("ok sending");

        return true;
    }
}
