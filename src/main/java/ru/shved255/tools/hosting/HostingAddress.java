package ru.shved255.tools.hosting;

import java.net.InetAddress;
import java.net.UnknownHostException;

import ru.shved255.tools.hosting.pinger.Pinger;

public class HostingAddress {
    private String ip;
    private String port;
    private Pinger pinger;

    public HostingAddress(String ip, String port, int timeout) {
        this.ip = ip;
        this.port = port;
        this.pinger = new Pinger(ip, Integer.valueOf(port), timeout);
    }

    public boolean fetch() {
        return this.pinger.fetchData();
    }

    public String getIp() {
        return this.ip;
    }

    public String getPort() {
        return this.port;
    }

    public Pinger getPinger() {
        return this.pinger;
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = prime * result + (this.ip == null ? 0 : this.ip.hashCode());
        result = prime * result + (this.port == null ? 0 : this.port.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        HostingAddress other = (HostingAddress) obj;
        if (this.ip == null ? other.ip != null : !this.ip.equals(other.ip)) {
            return false;
        }
        return this.port == null ? other.port == null : this.port.equals(other.port);
    }

    @Override
    public String toString() {
        return this.ip + ":" + this.port;
    }

    public static HostingAddress generateHM(Boolean flag, int timeout) {
        String[] chars = {"m", "n", "f", "d", "w", "s", "z"};
        int number = Choice.getRandomInt(1, 32);
        String let = chars[Choice.getRandomInt(0, chars.length - 1)];
        String num = String.valueOf(number);
        int prt = Choice.getRandomInt(25000, 27000);
        String port = String.valueOf(prt);
        String[] ends = {"ru", "xyz"};
        String ip = flag == null ? let + num + ".joinserver." + ends[Choice.getRandomInt(0, 1)] 
                                  : (flag ? let + num + ".joinserver." + ends[0] : let + num + ".joinserver." + ends[1]);
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateHM(flag, timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }

    public static HostingAddress generateCH(int timeout) {
        String begin = "45.93.200";
        String node = String.valueOf(Choice.getRandomInt(1, 30));
        String port = String.valueOf(Choice.getRandomInt(25000, 27000));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    public static HostingAddress generateAU(int timeout) {
        String begin = "49.12.87";
        String node = String.valueOf(Choice.getRandomInt(231, 231));
        String port = String.valueOf(Choice.getRandomInt(20000, 35000));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    public static HostingAddress generateAU1(int timeout) {
        String begin = "116.202.48";
        String node = String.valueOf(Choice.getRandomInt(240, 240));
        String port = String.valueOf(Choice.getRandomInt(20000, 35000));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    public static HostingAddress generateAU2(int timeout) {
        String begin = "49.12.82";
        String node = String.valueOf(Choice.getRandomInt(39, 39));
        String port = String.valueOf(Choice.getRandomInt(20000, 35000));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    public static HostingAddress generateUS(int timeout) {
        String begin = "45.133.9";
        String node = String.valueOf(Choice.getRandomInt(167, 167));
        String port = String.valueOf(Choice.getRandomInt(25000, 28000));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    public static HostingAddress generateZ(int timeout) {
        String begin = "5.188.118";
        String node = String.valueOf(Choice.getRandomInt(142, 142));
        String port = String.valueOf(Choice.getRandomInt(30000, 34000));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    
    public static HostingAddress generateZ1(int timeout) {
        String begin = "92.53.65";
        String node = String.valueOf(Choice.getRandomInt(136, 136));
        String port = String.valueOf(Choice.getRandomInt(30000, 32000));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    public static HostingAddress generateMS(int timeout) {
        String begin = "217.106.107";
        String node = String.valueOf(Choice.getRandomInt(176, 176));
        String port = String.valueOf(Choice.getRandomInt(25565, 32500));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    public static HostingAddress generateMS1(int timeout) {
        String begin = "212.22.93";
        String node = String.valueOf(Choice.getRandomInt(69, 69));
        String port = String.valueOf(Choice.getRandomInt(25000, 30000));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    public static HostingAddress generateBH(int timeout) {
        int number = Choice.getRandomInt(1, 7);
        String num = String.valueOf(number);
        int prt = Choice.getRandomInt(25000, 29000);
        String port = String.valueOf(prt);
        String ip = "ru" + num + ".mineserv.su";
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateBH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    
    public static HostingAddress generateSR(int timeout) {
        String begin = "217.106.107";
        String node = String.valueOf(Choice.getRandomInt(111, 111));
        String port = String.valueOf(Choice.getRandomInt(20000, 21000));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    public static HostingAddress generateGA(int timeout) {
        String begin = "65.108.206";
        String node = String.valueOf(Choice.getRandomInt(102, 102));
        String port = String.valueOf(Choice.getRandomInt(25000, 28000));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    public static HostingAddress generateGA1(int timeout) {
        String begin = "65.108.227";
        String node = String.valueOf(Choice.getRandomInt(231, 231));
        String port = String.valueOf(Choice.getRandomInt(25000, 26000));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    public static HostingAddress generateRE(int timeout) {
        String begin = "188.127.241";
        String node = String.valueOf(Choice.getRandomInt(8, 8));
        String port = String.valueOf(Choice.getRandomInt(25000, 26000));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }
    public static HostingAddress generateOU(int timeout) {
        String begin = "95.217.79";
        String node = String.valueOf(Choice.getRandomInt(25, 25));
        String port = String.valueOf(Choice.getRandomInt(25500, 26000));
        String ip = begin + "." + node; 
        try {
            InetAddress.getByName(ip);
        } catch (UnknownHostException ex) {
            return HostingAddress.generateCH(timeout);
        }
        HostingAddress adr = new HostingAddress(ip, port, timeout);
        return adr;
    }

    public static HostingAddress generate(Boolean flag, int timeout) {	
        int randomChoice = Choice.getRandomInt(0, 13);
        switch (randomChoice) {
            case 0:
                return HostingAddress.generateHM(flag, timeout);
            case 1:
            	return HostingAddress.generateCH(timeout);    
            case 2:
                return HostingAddress.generateBH(timeout);
            case 3:
                return HostingAddress.generateAU(timeout);
            case 4:
                return HostingAddress.generateAU1(timeout);   
            case 5:
                return HostingAddress.generateAU2(timeout);
            case 6:
                return HostingAddress.generateUS(timeout);
            case 7:
                return HostingAddress.generateZ(timeout);
            case 8:
                return HostingAddress.generateZ1(timeout);  
            case 9:
                return HostingAddress.generateMS(timeout);  
            case 10:
                return HostingAddress.generateMS1(timeout);    
            case 11:
                return HostingAddress.generateSR(timeout);    
            case 12:
                return HostingAddress.generateGA(timeout);   
            case 13:
                return HostingAddress.generateGA1(timeout); 
            case 14:
                return HostingAddress.generateRE(timeout);   
            case 15:
                return HostingAddress.generateOU(timeout);   
            default:     
                return HostingAddress.generateHM(flag, timeout);
                
        }
    }
}
