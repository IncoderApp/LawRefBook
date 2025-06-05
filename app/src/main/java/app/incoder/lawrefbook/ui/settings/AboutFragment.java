/*
 * Copyright (C) 2022 The Jerry xu Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.incoder.lawrefbook.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.Map;

import app.incoder.lawrefbook.R;
import app.incoder.lawrefbook.util.IntentAction;

/**
 * AboutFragment
 *
 * @author : Jerry xu
 * @since : 2022/5/2 15:31
 */
public class AboutFragment extends Fragment {

    public AboutFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView appTip = requireView().findViewById(R.id.tv_app_tip);
        appTip.setText(Html.fromHtml(getResources().getString(R.string.app_tip), Html.FROM_HTML_MODE_LEGACY));

        TextView thank = requireView().findViewById(R.id.tv_thank);
        thank.setMovementMethod(LinkMovementMethod.getInstance());

        RelativeLayout version = requireView().findViewById(R.id.rl_version);
        TextView versionName = requireView().findViewById(R.id.tv_version_number);
        RelativeLayout releaseNote = requireView().findViewById(R.id.rl_log);
        RelativeLayout sourceCode = requireView().findViewById(R.id.rl_code);
        RelativeLayout privacy = requireView().findViewById(R.id.rl_privacy);
        RelativeLayout praise = requireView().findViewById(R.id.rl_praise);

        RelativeLayout author = requireView().findViewById(R.id.rl_author);
        RelativeLayout email = requireView().findViewById(R.id.rl_email);
        Map<String, String> versionMap = getVersion(requireContext());
        String name = versionMap.get("versionName");
        String code = versionMap.get("versionCode");
        versionName.setText(String.format(getString(R.string.other_version_name), name, code));

        version.setOnClickListener(v -> {

        });

        releaseNote.setOnClickListener(v -> requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.lawrefbook_log)))));
        sourceCode.setOnClickListener(v -> requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_lawrefbook)))));
        privacy.setOnClickListener(v -> requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacy_address)))));
        praise.setOnClickListener(v -> openMarket(requireContext()));
        author.setOnClickListener(v -> requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.author_blog)))));
        email.setOnClickListener(v -> IntentAction.sendEmail(requireContext(), String.format(getString(R.string.content_feedback), getResources().getString(R.string.app_name)), "", getString(R.string.author_email)));
    }

    public static void openMarket(Context context) {
        try {
            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, context.getResources().getString(R.string.app_market), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static Map<String, String> getVersion(Context context) {
        Map<String, String> result = new HashMap<>(2);
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            // 版本的名称 用于显示在welcome界面下角
            String version = info.versionName;
            // 版本的 code 用于比较升级
            long code;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                code = info.getLongVersionCode();
            } else {
                code = info.versionCode;
            }
            result.put("versionName", version);
            result.put("versionCode", code + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
