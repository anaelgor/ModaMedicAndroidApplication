package com.example.modamedicandroidapplication;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import Model.Exceptions.ServerFalseException;
import Model.Users.Login;
import Model.Utils.HttpRequests;

//@RunWith(PowerMockRunner.class)

public class LoginTest {

    @Mock
    Context context;

    @Mock
    @InjectMocks
    HttpRequests httpRequests;

    @Before
    public void setUp() {

    }
    @Test
    public void askForChangePasswordTest() throws JSONException, ServerFalseException {

        //Given
        String token = "1234";
        Mockito.when(Login.getToken(Mockito.any())).thenReturn(token);
        JSONObject json = new JSONObject();
        json.put("data","test");
        Mockito.when(httpRequests.sendPostRequest(Mockito.any(),Mockito.anyString())).thenReturn(json);
        //When
        boolean result = Login.askForChangePassword(httpRequests);
        Assert.assertTrue(result);

}

}