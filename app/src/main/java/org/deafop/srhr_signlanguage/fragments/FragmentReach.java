package org.deafop.srhr_signlanguage.fragments;

import static android.app.ProgressDialog.show;
import static androidx.core.content.ContextCompat.getSystemService;
import static org.deafop.srhr_signlanguage.utils.Constant.VIDEO_LIST_COMPACT;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.deafop.srhr_signlanguage.R;
import org.deafop.srhr_signlanguage.activities.ActivityVideoDetail;
import org.deafop.srhr_signlanguage.activities.MainActivity;
import org.deafop.srhr_signlanguage.adapters.AdapterRecent;
import org.deafop.srhr_signlanguage.callbacks.CallbackListVideo;
import org.deafop.srhr_signlanguage.config.AppConfig;
import org.deafop.srhr_signlanguage.models.Video;
import org.deafop.srhr_signlanguage.rests.ApiInterface;
import org.deafop.srhr_signlanguage.rests.RestAdapter;
import org.deafop.srhr_signlanguage.utils.Constant;
import org.deafop.srhr_signlanguage.utils.EqualSpacingItemDecoration;
import org.deafop.srhr_signlanguage.utils.SharedPref;
import org.deafop.srhr_signlanguage.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentReach extends Fragment {

    View root_view, parent_view;

    private Call<CallbackListVideo> callbackCall = null;
    SharedPref sharedPref;
    EditText share,name,email,phone,subject;
    Button reach, book;
    private String shareMessage, nameMessage, emailMessage, phoneMessage, subjectMessage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root_view = inflater.inflate(R.layout.fragment_reach, container, false);
        parent_view = getActivity().findViewById(R.id.lyt_content);

        sharedPref = new SharedPref(getActivity());
        sharedPref.setDefaultSortHome();

        setHasOptionsMenu(true);
        share = root_view.findViewById(R.id.edit_share);
        name = root_view.findViewById(R.id.edit_name);
        email = root_view.findViewById(R.id.edit_email);
        phone = root_view.findViewById(R.id.edit_phone);
        subject = root_view.findViewById(R.id.edit_subject);
        reach = root_view.findViewById(R.id.btn_reach);
        book = root_view.findViewById(R.id.btn_book);


        reach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] TO_EMAILS = {"deafop@gmail.com", "support@deafopkenya.org"};
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, TO_EMAILS);
                intent.putExtra(Intent.EXTRA_SUBJECT, "SRHR SHARED VIEWS");
                intent.putExtra(Intent.EXTRA_TEXT, share.getText()).toString();

                startActivity(Intent.createChooser(intent, "Choose your email client"));
            }
        });

        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(phone.getText().toString())){
                    String[] TO_EMAILS = {"deafop@gmail.com", "support@deafopkenya.org"};
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, TO_EMAILS);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "SRHR REQUEST FOR CALL");
                    intent.putExtra(Intent.EXTRA_TEXT, "Dear DEAFOP " +"\n My name is " + name.getText().toString() + " Please contact me on my email " + email.getText().toString() + " The Subject of the talk will be " + subject.getText().toString() + ".");

                    startActivity(Intent.createChooser(intent, "Choose your email client"));
                }
                else {
                    String[] TO_EMAILS = {"deafop@gmail.com", "support@deafopkenya.org"};
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, TO_EMAILS);
                    intent.putExtra(Intent.EXTRA_SUBJECT, "SRHR REQUEST FOR CALL");
                    intent.putExtra(Intent.EXTRA_TEXT, "Dear DEAFOP " +"\n My name is " + name.getText().toString() + " Please contact me on my email " + email.getText().toString() + " or phone number " + phone.getText().toString() + " The Subject of the talk will be " + subject.getText().toString() + ".");

                    startActivity(Intent.createChooser(intent, "Choose your email client"));

                }


            }
        });









        return root_view;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }







    @Override
    public void onDestroy() {
        super.onDestroy();
        if (callbackCall != null && callbackCall.isExecuted()) {
            callbackCall.cancel();
        }
    }









    @Override
    public void onResume() {
        super.onResume();
    }

}
