package ru.shved255.tools.bot;

import com.github.steveice10.mc.auth.service.SessionService;
import com.github.steveice10.mc.protocol.MinecraftProtocol;
import com.github.steveice10.mc.protocol.data.game.ClientCommand;
import com.github.steveice10.mc.protocol.data.game.entity.object.Direction;
import com.github.steveice10.mc.protocol.data.game.entity.player.HandPreference;
import com.github.steveice10.mc.protocol.data.game.entity.player.PlayerAction;
import com.github.steveice10.mc.protocol.data.game.entity.player.PositionElement;
import com.github.steveice10.mc.protocol.data.game.setting.ChatVisibility;
import com.github.steveice10.mc.protocol.data.game.setting.SkinPart;
import com.github.steveice10.mc.protocol.packet.common.clientbound.ClientboundKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.common.serverbound.ServerboundClientInformationPacket;
import com.github.steveice10.mc.protocol.packet.common.serverbound.ServerboundCustomPayloadPacket;
import com.github.steveice10.mc.protocol.packet.common.serverbound.ServerboundKeepAlivePacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.ClientboundLoginPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerCombatKillPacket;
import com.github.steveice10.mc.protocol.packet.ingame.clientbound.entity.player.ClientboundPlayerPositionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatCommandPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundChatPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.ServerboundClientCommandPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.level.ServerboundAcceptTeleportationPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundMovePlayerPosPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundPlayerActionPacket;
import com.github.steveice10.mc.protocol.packet.ingame.serverbound.player.ServerboundSetCarriedItemPacket;
import com.github.steveice10.packetlib.ProxyInfo;
import com.github.steveice10.packetlib.Session;
import com.github.steveice10.packetlib.event.session.DisconnectedEvent;
import com.github.steveice10.packetlib.event.session.SessionAdapter;
import com.github.steveice10.packetlib.packet.Packet;
import com.github.steveice10.packetlib.tcp.TcpClientSession;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.cloudburstmc.math.vector.Vector3i;

public class Bot {
   String ip;
   int port;
   int botsAmount;
   int threadsAmount;
   String botsName;
   String[] messages;
   boolean dropInv;
   boolean reconnect;
   boolean useProxy;
   int proxyType;
   HashMap<String, Integer> proxyServers = new HashMap<String, Integer>();
   List<String> proxyServersAsList;

   @SuppressWarnings("unused")
public void start() {
      try (Scanner scanner = new Scanner(System.in)) {
		System.out.print("Введите айпи и порт сервера (127.0.0.1:25565): ");
		  String[] ipParts = scanner.nextLine().split(":");
		  this.ip = ipParts[0];
		  this.port = Integer.parseInt(ipParts[1]);
		  System.out.print("Введите кол-во ботов (500): ");
		  this.botsAmount = Integer.parseInt(scanner.nextLine());
		  System.out.print("Введите кол-во ядер (15): ");
		  this.threadsAmount = Integer.parseInt(scanner.nextLine());
		  System.out.print("Введите имя ботов (bot_): ");
		  this.botsName = scanner.nextLine();
		  System.out.print("Введите сообщение которые будут вводить при входе боты. Для мульти сообщений используйте ';', чтобы разделить их. (/register 1234 1234;Hello): ");
		  this.messages = scanner.nextLine().split(";");
		  System.out.print("Боты при входе будут выбрасывать предметы? y/n (y): ");
		  this.dropInv = scanner.nextLine().equals("y");
		  System.out.print("Боты будут переподключаться? y/n (y): ");
		  this.reconnect = scanner.nextLine().equals("y");
		  System.out.print("Использовать прокси? y/n (y): ");
		  this.useProxy = scanner.nextLine().equals("y");
		  if (this.useProxy) {
		     System.out.println("1. SOCKS4\n");
		     System.out.println("2. SOCKS5\n");
		     System.out.println("3. HTTP\n");
		     System.out.println("Выберите тип прокси: ");
		     this.proxyType = Integer.parseInt(scanner.nextLine());
		     System.out.println("Введите путь до файла с прокси, у которых формат ip:port: ");
		     String proxyPath = scanner.next();
		     if(useProxy) {
		     if(proxyPath == null) return;
		     }
		     try {
		        Files.readAllLines(Paths.get(proxyPath)).forEach((line) -> {
		           String[] proxyParts = line.split(":");
		           if (proxyParts.length == 2) {
		              this.proxyServers.put(proxyParts[0], Integer.parseInt(proxyParts[1]));
		           }

		        });
		     } catch (IOException var7) {
		     }

		     this.proxyServersAsList = new ArrayList<String>(this.proxyServers.keySet());
		  }
	} catch (NumberFormatException e) {
		e.printStackTrace();
	}
      System.out.println("Создание ботов....");
      ExecutorService executor = Executors.newFixedThreadPool(this.threadsAmount);

      for(int botId_ = 1; botId_ < botsAmount + 1; ++ botId_) {
    	 final int finalBotId = botId_;
         executor.submit(() -> {
            final String botName = botsName + finalBotId;
            SessionService sessionService = new SessionService();
            final TcpClientSession bot;
            if (this.useProxy) {
               String randomProxyIp = this.proxyServersAsList.get((new Random()).nextInt(this.proxyServersAsList.size()));
               Integer randomProxyPort = this.proxyServers.get(randomProxyIp);
               ProxyInfo proxyInfo = null;
               switch(this.proxyType) {
               case 1:
                  sessionService.setProxy(new Proxy(Type.SOCKS, new InetSocketAddress(randomProxyIp, randomProxyPort)));
                  proxyInfo = new ProxyInfo(com.github.steveice10.packetlib.ProxyInfo.Type.SOCKS4, new InetSocketAddress(randomProxyIp, randomProxyPort));
                  break;
               case 2:
                  sessionService.setProxy(new Proxy(Type.SOCKS, new InetSocketAddress(randomProxyIp, randomProxyPort)));
                  proxyInfo = new ProxyInfo(com.github.steveice10.packetlib.ProxyInfo.Type.SOCKS5, new InetSocketAddress(randomProxyIp, randomProxyPort));
                  break;
               case 3:
                   sessionService.setProxy(new Proxy(Type.HTTP, new InetSocketAddress(randomProxyIp, randomProxyPort)));
                   proxyInfo = new ProxyInfo(com.github.steveice10.packetlib.ProxyInfo.Type.HTTP, new InetSocketAddress(randomProxyIp, randomProxyPort));
                   break;
               default:
                  sessionService.setProxy(Proxy.NO_PROXY);
               }

               bot = new TcpClientSession(this.ip, this.port, new MinecraftProtocol(botName), proxyInfo);
            } else {
               bot = new TcpClientSession(this.ip, this.port, new MinecraftProtocol(botName));
            }

            bot.setFlag("session-service", sessionService);
            bot.addListener(new SessionAdapter() {
               final Executor delayedExecutor = Executors.newScheduledThreadPool(1);;
               volatile boolean loggedIn;
               int tickCounter;
               Session currentSession;
               final Bot.Vec3D currentPositionVector;
               volatile boolean falling;
               volatile boolean corrected;
               volatile boolean firstPosPacket;
               final Bot.Vec3D fallSpeedVector;
               volatile boolean onGround;
               final Bot.Vec3D moveVec;
               int viewDistance;

               {
            	  final ScheduledExecutorService delayedExecutor = Executors.newScheduledThreadPool(1);
                  this.tickCounter = 0;
                  this.currentPositionVector = new Bot.Vec3D(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
                  this.falling = false;
                  this.corrected = false;
                  this.firstPosPacket = false;
                  this.fallSpeedVector = new Bot.Vec3D(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
                  this.onGround = false;
                  this.moveVec = new Bot.Vec3D(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
               }

               public void packetReceived(Session session, Packet packet) {
                  if(packet instanceof ClientboundLoginPacket) {
                     this.currentSession = session;
                     this.loggedIn = true;
                     this.viewDistance = ((ClientboundLoginPacket)packet).getViewDistance();
                     System.out.println("Бот '" + botName + "' присоиденился.");
                     session.send(new ServerboundCustomPayloadPacket("minecraft:brand", new byte[]{5, 80, 97, 112, 101, 114}));
                     ((ScheduledExecutorService) delayedExecutor).schedule(() -> {
                    	 session.send(new ServerboundClientInformationPacket("en_GB", this.viewDistance, ChatVisibility.FULL, true, Arrays.asList(SkinPart.values()), HandPreference.RIGHT_HAND, false, true));
                        this.doTick();
                     }, 1, TimeUnit.MILLISECONDS);
                     Arrays.stream(Bot.this.messages).forEach((message) -> {
                        if (!message.isEmpty()) {
                           if (message.startsWith("/")) {
                              session.send(new ServerboundChatCommandPacket(message.substring(1), Instant.now().toEpochMilli(), 0L, Collections.emptyList(), 0, new BitSet()));
                           } else {
                              session.send(new ServerboundChatPacket(message, Instant.now().toEpochMilli(), 0L, (byte[])null, 0, new BitSet()));
                           }
                        }

                     });
                     if (Bot.this.dropInv) {
                        (new Thread(() -> {
                           for(int slotId = 1; slotId < 9; ++slotId) {
                              try {
                                 session.send(new ServerboundSetCarriedItemPacket(slotId));
                                 Thread.sleep(200L);
                                 session.send(new ServerboundPlayerActionPacket(PlayerAction.DROP_ITEM_STACK, Vector3i.FORWARD, Direction.DOWN, slotId));
                                 Thread.sleep(100L);
                              } catch (InterruptedException var3) {
                              }
                           }

                        })).start();
                     }
                  } else if (packet instanceof ServerboundKeepAlivePacket) {
                     session.send(new ClientboundKeepAlivePacket(((ServerboundKeepAlivePacket)packet).getPingId()));
                  } else if (packet instanceof ClientboundPlayerCombatKillPacket) {
                     session.send(new ServerboundClientCommandPacket(ClientCommand.RESPAWN));
                     System.out.println("Бот '" + botName + "' умер и респавнился.");
                  } else if (packet instanceof ClientboundPlayerPositionPacket) {
                     session.send(new ServerboundAcceptTeleportationPacket(((ClientboundPlayerPositionPacket)packet).getTeleportId()));
                     if (!this.firstPosPacket) {
                        this.corrected = true;
                        this.firstPosPacket = true;
                     }

                     double lastY = this.currentPositionVector.getY();
                     this.currentPositionVector.setX(((ClientboundPlayerPositionPacket)packet).getRelative().contains(PositionElement.X) ? ((ClientboundPlayerPositionPacket)packet).getX() + this.currentPositionVector.getX() : ((ClientboundPlayerPositionPacket)packet).getX());
                     this.currentPositionVector.setY(((ClientboundPlayerPositionPacket)packet).getRelative().contains(PositionElement.Y) ? ((ClientboundPlayerPositionPacket)packet).getY() + this.currentPositionVector.getY() : ((ClientboundPlayerPositionPacket)packet).getY());
                     this.currentPositionVector.setZ(((ClientboundPlayerPositionPacket)packet).getRelative().contains(PositionElement.Z) ? ((ClientboundPlayerPositionPacket)packet).getZ() + this.currentPositionVector.getZ() : ((ClientboundPlayerPositionPacket)packet).getZ());
                     this.currentPositionVector.setYaw(((ClientboundPlayerPositionPacket)packet).getRelative().contains(PositionElement.YAW) ? (double)((ClientboundPlayerPositionPacket)packet).getYaw() + this.currentPositionVector.getYaw() : (double)((ClientboundPlayerPositionPacket)packet).getYaw());
                     this.currentPositionVector.setPitch(((ClientboundPlayerPositionPacket)packet).getRelative().contains(PositionElement.PITCH) ? (double)((ClientboundPlayerPositionPacket)packet).getPitch() + this.currentPositionVector.getPitch() : (double)((ClientboundPlayerPositionPacket)packet).getPitch());
                     if (this.currentPositionVector.getY() - lastY > 0.0D && this.fallSpeedVector.getY() < 0.0D) {
                        this.onGround = true;
                        this.fallSpeedVector.setY(0.0D);
                     }
                  }

               }

               public void disconnected(DisconnectedEvent event) {
                  String var10001 = botName;
                  System.out.println("Бот '" + var10001 + "' кикнут, по причине: '" + event.getCause().getMessage() + "'.");
                  if (Bot.this.reconnect) {
                     System.out.println("Бот '" + botName + "' переподключается...");
                     bot.disconnect("");
                     bot.connect();
                  }

               }

               private void doTick() {
                  if (this.loggedIn) {
                     this.delayedExecutor.execute(this::doTick);
                     ++this.tickCounter;
                     if (this.tickCounter % 5 == 0 || this.fallSpeedVector.getY() < 0.0D) {
                        this.falling = true;
                        if (this.fallSpeedVector.getY() > -3.9200038147008747D) {
                           this.fallSpeedVector.setRelatively(-0.08D, Bot.Vec3D.Relative.Y);
                           this.fallSpeedVector.setY(this.fallSpeedVector.getY() * 0.98D);
                        }
                     }

                     double currY = this.currentPositionVector.getY();
                     if (currY - Math.floor(currY) > 0.0D && !this.falling) {
                        this.currentPositionVector.setRelatively(Math.floor(currY) - currY, Bot.Vec3D.Relative.Y);
                     }

                     this.currentPositionVector.setRelatively(this.fallSpeedVector.getY(), Bot.Vec3D.Relative.Y);
                     this.currentPositionVector.setRelatively(this.moveVec.getX(), Bot.Vec3D.Relative.X);
                     this.currentPositionVector.setRelatively(this.moveVec.getY(), Bot.Vec3D.Relative.Y);
                     this.currentPositionVector.setRelatively(this.moveVec.getZ(), Bot.Vec3D.Relative.Z);
                     this.currentSession.send(new ServerboundMovePlayerPosPacket(this.onGround, this.currentPositionVector.getX(), this.currentPositionVector.getY(), this.currentPositionVector.getZ()));
                     if (!this.firstPosPacket) {
                        this.onGround = true;
                     }

                     try {
                        this.onGround = !this.falling || this.corrected;
                     } finally {
                        this.falling = false;
                        this.corrected = false;
                     }

                  }
               }
            });
            bot.connect();
         });
      }

      executor.shutdown();

      try {
         executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
      } catch (InterruptedException var6) {
         Thread.currentThread().interrupt();
      }

   }

   public static class Vec3D {
      private double x;
      private double y;
      private double z;
      private double yaw;
      private double pitch;

      public Vec3D(double x, double y, double z, double yaw, double pitch) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.yaw = yaw;
         this.pitch = pitch;
      }

      public synchronized double getPitch() {
         return this.pitch;
      }

      public synchronized double getYaw() {
         return this.yaw;
      }

      public synchronized void setPitch(double pitch) {
         this.pitch = pitch;
      }

      public synchronized void setYaw(double yaw) {
         this.yaw = yaw;
      }

      public synchronized double getX() {
         return this.x;
      }

      public synchronized double getY() {
         return this.y;
      }

      public synchronized double getZ() {
         return this.z;
      }

      public synchronized void setX(double x) {
         this.x = x;
      }

      public synchronized void setY(double y) {
         this.y = y;
      }

      public synchronized void setZ(double z) {
         this.z = z;
      }

      public synchronized void setRelatively(double delta, Bot.Vec3D.Relative target) {
         switch(target) {
         case X:
            this.x += delta;
            break;
         case Y:
            this.y += delta;
            break;
         case Z:
            this.z += delta;
            break;
         case YAW:
            this.yaw += delta;
            break;
         case PITCH:
            this.pitch += delta;
         }

      }

      public static enum Relative {
         X,
         Y,
         Z,
         YAW,
         PITCH;


         @SuppressWarnings("unused")
		 private static Bot.Vec3D.Relative[] $values() {
            return new Bot.Vec3D.Relative[]{X, Y, Z, YAW, PITCH};
         }
      }
   }
}

