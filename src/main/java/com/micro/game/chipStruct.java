package com.micro.game;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document
class ChipStruct{
    public long betTarget;
    public long betAmount;
    public ChipStruct(int idenx){
        betTarget=idenx;
        betAmount=0;
    }
    public ChipStruct(){
        betAmount=0;
    }
}