package com.example.mychats.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mychats.Chat;
import com.example.mychats.ModelClasses.RecentModel;
import com.example.mychats.ModelClasses.UserModel;
import com.example.mychats.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.observers.TestObserver;

public class RecentChatUsersAdapter extends RecyclerView.Adapter<RecentChatUsersAdapter.Holder> {

    List<String > list;
    Context context;

    public RecentChatUsersAdapter(List<String > list, Context context ) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_for_recyclerview_my_chat, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, int position) {

        final String userProfileLink = list.get(position);



        FirebaseDatabase.getInstance().getReference("Users").child(userProfileLink)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);

                        if( ! TextUtils.isEmpty(userModel.getProfileLink()) )
                            holder.setImage(userModel.getProfileLink());

                        holder.setName(userModel.getName());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chat = new Intent(context, Chat.class);
                chat.putExtra("UserProfileLink", userProfileLink);
                context.startActivity(chat);
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        private CircleImageView recentUserImage;
        private TextView recentUserName;

        public Holder(@NonNull View itemView) {
            super(itemView);

            recentUserImage = itemView.findViewById(R.id.recentUserImage);
            recentUserName = itemView.findViewById(R.id.recentUserName);
        }

        public void setImage(String url) {
            Picasso.get().load(url).placeholder(R.drawable.profile_pic_default).into(recentUserImage);
        }
        public void setName(String name) {
            recentUserName.setText(name);
        }
    }
}
