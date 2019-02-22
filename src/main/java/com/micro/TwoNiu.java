
package com.micro;

import com.micro.game.TNGameMain;

import frame.config.Config;
import frame.socket.NettyServer;
import frame.util.ThreadPoolExecutorUtils;

/**
 * @author sam
 * @ClassName: GameServerApplication
 * @Description: 游戏服务启动类
 * @date 2018-07-23
 */

public class TwoNiu {

	public static void main(String[] args){
		ThreadPoolExecutorUtils.getInstance().execute(() -> {
            try {
                new NettyServer().start(6006);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //给mongo.http赋值


        System.out.println(args.length ==0);

        if(args.length ==0){
            Config.ACCOUNT_HOST= "172.20.100.156";
            Config.GAME_HOST = "172.20.100.233";
            Config.MONGO_HOST = "172.20.101.68";
        }
        else if (args[0].equals("test")) {
            Config.ACCOUNT_HOST= "172.20.100.156";
            Config.GAME_HOST = "172.20.100.233";
            Config.MONGO_HOST = "172.20.101.68";
        }else if (args[0].equals("prod")){
            Config.ACCOUNT_HOST = "172.20.101.68";
            Config.GAME_HOST = "172.20.101.68";
            Config.MONGO_HOST = "172.20.101.68";

        }else{
            System.out.println("启动报错");
        }

        Config.ACCOUNT_PORT = "8080";
        Config.GAME_PORT = "9501";
        Config.MONGO_PORT = 27017;

		// 游戏主循环
		(new TNGameMain()).run();
	}

}

