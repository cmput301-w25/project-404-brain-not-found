package com.example.cmput301_team_project.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.db.FirebaseAuthenticationService;
import com.example.cmput301_team_project.db.UserDatabaseService;
import com.google.android.gms.tasks.Task;

import java.util.List;
/**
 * Adapter class for managing a list of follow requests.
 */
public class RequestListAdapter extends ArrayAdapter<String> {
    private final UserDatabaseService userDatabaseService;
    /**
     * Constructor for RequestListAdapter.
     *
     * @param context The context of the application.
     * @param objects The list of usernames representing follow requests.
     */
    public RequestListAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
        userDatabaseService = UserDatabaseService.getInstance();
    }

    /**
     * Gets the view for an individual follow request item.
     *
     * @param position The position of the item in the list.
     * @param convertView The recycled view to populate.
     * @param parent The parent view that this view will be attached to.
     * @return The populated view representing a follow request.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if(convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.request_list_content, parent, false);
        }
        else {
            view = convertView;
        }
        String follower = getItem(position);
        String currentUser = FirebaseAuthenticationService.getInstance().getCurrentUser();

        TextView requestText = view.findViewById(R.id.request_text);
        String text = "@" + follower + " " + getContext().getString(R.string.follow_request);

        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, 1 + follower.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        requestText.setText(spannable);

        Button acceptButton = view.findViewById(R.id.accept_button);
        Button declineButton = view.findViewById(R.id.decline_button);

        acceptButton.setOnClickListener(v -> userDatabaseService.acceptRequest(follower, currentUser).addOnSuccessListener(d -> remove(follower)));
        declineButton.setOnClickListener(v -> userDatabaseService.removeRequest(follower, currentUser).addOnSuccessListener(n -> remove(follower)));

        return view;
    }

    /**
     * Refreshes the list of follow requests by retrieving the latest requests from the database.
     *
     * @return A {@link Task} that resolves with the number of follow requests retrieved.
     */
    public Task<Integer> refreshRequests() {
        clear();

        return userDatabaseService.getRequests(FirebaseAuthenticationService.getInstance().getCurrentUser())
                .continueWith(task -> {
                    if(task.isSuccessful()) {
                        addAll(task.getResult());
                        notifyDataSetChanged();
                        return task.getResult().size();
                    }
                    return 0;
                });
    }
}
