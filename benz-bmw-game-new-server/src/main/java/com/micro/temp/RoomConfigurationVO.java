package com.micro.temp;

import com.micro.common.bean.BaseModel;
import com.micro.common.bean.account.model.TbGameRoom;
import com.micro.common.bean.account.model.TbGameRoomConfigurationBet;
import lombok.Data;

import java.io.Serializable;

@Data
public class RoomConfigurationVO extends BaseModel implements Serializable {

	private static final long serialVersionUID = -1L;
	private TbGameRoom tbGameRoom;
	private TbGameRoomConfigurationBet tbRoomConfig;

}