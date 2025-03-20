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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import com.example.cmput301_team_project.R;
import com.example.cmput301_team_project.SessionManager;
import com.example.cmput301_team_project.db.UserDatabaseService;

import java.util.List;

public class RequestListAdapter extends ArrayAdapter<String> {
    private final UserDatabaseService userDatabaseService;
    public RequestListAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
        userDatabaseService = UserDatabaseService.getInstance();
    }

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
        String username = SessionManager.getInstance().getCurrentUser();

        TextView requestText = view.findViewById(R.id.request_text);
        String text = "@" + username + " " + getContext().getString(R.string.follow_request);

        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, 1 + username.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        requestText.setText(spannable);

        return view;
    }

    public void refreshRequests() {
        clear();

        userDatabaseService.getRequests(SessionManager.getInstance().getCurrentUser())
                .addOnSuccessListener(this::addAll);
    }
}
