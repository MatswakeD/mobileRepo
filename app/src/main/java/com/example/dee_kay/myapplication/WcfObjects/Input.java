package com.example.dee_kay.myapplication.WcfObjects;

import java.io.Serializable;

/**
 * Created by DEE-KAY on 2017/05/06.
 */

public class Input implements Serializable
{

    public User user;

    public Input()
    {
        this.user = new User();
    }


}

