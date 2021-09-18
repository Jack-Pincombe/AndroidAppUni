package com.example.youtubefirebase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User {

    public String name, age, email;

    public Set<User> friends, pendingFriends;
    public User(){

    }

    public User(String email, String name, String age) {
        this.name = name;
        this.age=age;
        this.email=email;
//        this.friends = new HashSet<>();
//        this.pendingFriends = new HashSet<>();
    }

    public void addPendingFriend(User friend) {
        pendingFriends.add(friend);
    }

    public void addFriend(User friend) {
        friends.add(friend);
    }
}
