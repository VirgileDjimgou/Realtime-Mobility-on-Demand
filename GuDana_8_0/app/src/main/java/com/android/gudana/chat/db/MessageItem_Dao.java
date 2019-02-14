package com.android.gudana.chat.db;


import com.android.gudana.chat.model.MessageItem;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.android.persistence.codelab.db.user.User;
import com.android.gudana.chat.model.MessageItem;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;

@Dao
public interface MessageItem_Dao {
    @Query("select * from MessageItem")
    List<MessageItem> loadAllMessage();

    @Insert(onConflict = IGNORE)
    void insertMessage(MessageItem user);

    @Delete
    void deleteMessage(MessageItem Msg);


    @Insert(onConflict = IGNORE)
    void insertOrReplaceUsers(MessageItem... Msg_s);

}