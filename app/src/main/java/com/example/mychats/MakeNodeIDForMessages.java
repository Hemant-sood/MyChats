package com.example.mychats;

/*
If person A's id is 'aaabbb' and person B's id is 'eeeeeee', you create a thread between them with a node 'aaabbbeeeeeee' and all their messages are stored at that node.
In this case, when loading conversations between the two, say in a list, you query just that endpoint. An example of setting user endpoint is shown below
Function setsup endpoint for one to one chat*/


public class MakeNodeIDForMessages {


    public static String setOneToOneChat(String fromUid1, String toUid2) {
        //Check if user1’s id is less than user2's
        if( fromUid1.compareTo(toUid2) < 0){
            return fromUid1 + toUid2;
        }
        else{
            return toUid2 + fromUid1;
        }

    }

}
