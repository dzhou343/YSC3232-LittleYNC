package com.example.littleync.controller;

public class UserNameAlreadyUsed extends Exception{

    public UserNameAlreadyUsed(){
        super("Username is already taken!");
    }

}
