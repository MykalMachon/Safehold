package com.tinybox.safehold.ui.account.emergency_contact_preference;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.tinybox.safehold.R;
import com.tinybox.safehold.data.Contact;
import com.tinybox.safehold.data.EmergencyContactDataHandler;

import java.util.ArrayList;
import java.util.List;

public class EmergencyContactAdapter extends RecyclerView.Adapter<EmergencyContactAdapter.EmergencyContactHolder>{
    private List<Contact> contactList;
    private EmergencyContactDataHandler contactDataHandler;

    public EmergencyContactAdapter(List<Contact> contactList,  EmergencyContactDataHandler contactDataHandler) {
        this.contactList = new ArrayList<>(contactList);
        this.contactDataHandler = contactDataHandler;

    }

    @NonNull
    @Override
    public EmergencyContactHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_emergency_contact_item, parent, false);
        EmergencyContactHolder vh = new EmergencyContactHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull EmergencyContactHolder holder, final int position) {
        holder.emergencyContactName.setText(contactList.get(position).getName());
        holder.emergencyContactPhoneNumber.setText(contactList.get(position).getPhoneNumber());

        holder.emergencyContactDeleteButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.wtf("deleted", "Deleted: "+ contactList.get(position).getId());
                        contactDataHandler.deleteContactWithID(contactList.get(position).getId());
                        contactList.remove(position);
                        notifyItemRemoved(position);

                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class EmergencyContactHolder extends RecyclerView.ViewHolder{
        public TextView emergencyContactName;
        public TextView emergencyContactPhoneNumber;
        public ImageView emergencyContactImage;
        public ImageView emergencyContactDeleteButton;

        public EmergencyContactHolder(@NonNull View itemView) {
            super(itemView);
            emergencyContactName = itemView.findViewById(R.id.tvEmergencyContactName);
            emergencyContactPhoneNumber = itemView.findViewById(R.id.tvEmergencyContactPhoneNumber);
            emergencyContactImage = itemView.findViewById(R.id.ivEmergencyContactImage);
            emergencyContactDeleteButton = itemView.findViewById(R.id.bvDeleteEmergencyContact);
        }
    }
}
