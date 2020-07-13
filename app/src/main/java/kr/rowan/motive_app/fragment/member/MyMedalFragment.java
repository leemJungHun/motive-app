package kr.rowan.motive_app.fragment.member;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import kr.rowan.motive_app.R;
import kr.rowan.motive_app.activity.FamilyMainActivity;
import kr.rowan.motive_app.activity.MemberMainActivity;
import kr.rowan.motive_app.databinding.FragmentMymedalBinding;
import kr.rowan.motive_app.network.HttpRequestService;
import kr.rowan.motive_app.network.dto.GetAllMedalInfoRequest;
import kr.rowan.motive_app.network.vo.MedalInfoVO;
import kr.rowan.motive_app.network.vo.UserInfoVO;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyMedalFragment extends Fragment {

    private final String TAG = MyMedalFragment.class.getSimpleName();

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);

    private FragmentMymedalBinding binding;
    private UserInfoVO vo;
    private int mScrollViewHeight;
    private int mScreenWidth;
    private int mNewScroll;
    private int mapHeight = 0;
    private int fogHeight = 0;
    private int mapWidth = 0;
    private int duration = 2000;
    private double sizeValueX = 0;
    private double sizeValueY = 0;
    private int scrollValue = 1;
    private int scrollLimit = 0;
    private ArrayList<MedalInfoVO> medalInfoVOS = new ArrayList<>();
    private FragmentActivity activity;

    private int goldMedalCnt, silverMedalCnt, blackMedalCnt, totalPoint, currentCnt, weekCount;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        assert args != null;
        vo = (UserInfoVO) args.getSerializable("userInfoVO");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MemberMainActivity) {
            this.activity = (MemberMainActivity) context;
        } else if (context instanceof FamilyMainActivity) {
            this.activity = (FamilyMainActivity) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (enter && nextAnim == R.anim.pull_in_right || enter && nextAnim == R.anim.pull_in_left) {
            Animation anim = AnimationUtils.loadAnimation(activity, nextAnim);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public void onAnimationEnd(Animation animation) {
                    binding.medalMapScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                        Log.d("scrollY",scrollY+" ");
                        mScrollViewHeight = binding.medalResultLayout.getHeight();
                        if (scrollY>scrollLimit){
                            v.setScrollY(scrollLimit);
                        }
                        if (scrollY <= 300) {
                            //show achieve card
                            if (binding.medalResultLayout.getVisibility() == View.GONE) {
                                binding.medalResultLayout.setVisibility(View.VISIBLE);
                            }
                            binding.medalResultLayout.setY(0 - (mScrollViewHeight * (scrollY / 300.0f)));
                            binding.simpleMedalResultLayout.setY(-binding.simpleMedalResultLayout.getHeight());
                        } else {
                            if (scrollY < 600) {
                                mNewScroll = scrollY - 300;
                            } else {
                                mNewScroll = 300;
                            }
                            binding.simpleMedalResultLayout.setY(-binding.simpleMedalResultLayout.getHeight() + binding.simpleMedalResultLayout.getHeight() * (mNewScroll / (300.0f)));
                            if (binding.medalResultLayout.getVisibility() == View.VISIBLE) {
                                binding.medalResultLayout.setVisibility(View.GONE);
                            }
                        }

                    });
                    if (vo.getGroupCode() != null && !vo.getGroupCode().equals("null")) {
                        binding.centerValue.setVisibility(View.VISIBLE);
                        binding.lastMedal.setVisibility(View.VISIBLE);
                        setMedal();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            return anim;
        }
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_mymedal, container, false);
        try {
            currentCnt = currentWeekCount(vo.getStartDate());

        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.myNameTxtView.setText(vo.getName());
        binding.simpleMyNameTxtView.setText(vo.getName());

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        mScreenWidth = metrics.widthPixels;
        //retrofit
        //통신
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpRequestService.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HttpRequestService httpRequestService = retrofit.create(HttpRequestService.class);

        GetAllMedalInfoRequest request = new GetAllMedalInfoRequest();
        request.setUserId(vo.getId());
        request.setGroupCode(vo.getGroupCode());


        if (vo.getGroupCode() != null && !vo.getGroupCode().equals("null")) {
            httpRequestService.getAllMedalInfoRequest(request).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.body() != null) {
                        String result = response.body().get("result").toString();
                        if (!result.equals("null")) {
                            Gson gson = new Gson();
                            int medalCheck = 1;
                            JsonObject jsonObject = response.body();
                            JsonArray jsonArray = jsonObject.getAsJsonArray("result");
                            weekCount = jsonArray.size();
                            for (int index = 0; index < jsonArray.size(); index++) {
                                if (index < currentCnt) {
                                    final MedalInfoVO medalInfoVO = gson.fromJson(jsonArray.get(index).toString(), MedalInfoVO.class);
                                    medalInfoVOS.add(medalInfoVO);
                                    if (medalInfoVO.getGoldMedalCnt() == 1) {
                                        goldMedalCnt++;
                                        totalPoint += 100;
                                        medalCheck=0;
                                    } else if (medalInfoVO.getSilverMedalCnt() == 1) {
                                        silverMedalCnt++;
                                        totalPoint += 100;
                                        medalCheck=0;
                                    } else if (index!=0&&index!=currentCnt-1) {
                                        blackMedalCnt++;
                                        totalPoint -= 50;
                                        medalCheck=0;
                                    }
                                }
                            }
                            binding.goldCntTxtView.setText(String.valueOf(goldMedalCnt));
                            binding.silverCntTxtView.setText(String.valueOf(silverMedalCnt));
                            binding.blackCntTxtView.setText(String.valueOf(blackMedalCnt));
                            String totalCnt = totalPoint + "만냥";
                            binding.totalCntTxtView.setText(totalCnt);
                            Log.e("jsonArray.size",jsonArray.size()+" ");
                            double achievePercentage = ((double) (currentCnt-medalCheck) / jsonArray.size()) * 100.0;
                            achievePercentage = Double.parseDouble(String.format(Locale.KOREAN, "%.1f", achievePercentage));

                            String myGoal = "진행률 " + achievePercentage + "%";
                            binding.myGoalTxtView.setText(myGoal);
                            if (achievePercentage>0){
                                binding.achieveView.setVisibility(View.VISIBLE);
                                binding.achieveBarGreen.setVisibility(View.VISIBLE);
                            }
                            Log.e("achievePercentage"," " + achievePercentage);
                            binding.achieveView.getLayoutParams().width = (int) (mScreenWidth * (achievePercentage / 100));
                            int barWidth = binding.barGray.getWidth();
                            Log.e("achievePercentage", (int) (barWidth * (achievePercentage / 100)) + " ");
                            binding.achieveBarGreen.getLayoutParams().width = (int) (barWidth * (achievePercentage / 100));

                            binding.simpleGoldCntTxtView.setText(String.valueOf(goldMedalCnt));
                            binding.simpleSilverCntTxtView.setText(String.valueOf(silverMedalCnt));
                            binding.simpleBlackCntTxtView.setText(String.valueOf(blackMedalCnt));
                            totalCnt = totalPoint + "만냥";
                            binding.simpleTotalCntTxtView.setText(totalCnt);

                            Log.e(TAG, "medal info \n gold = " + goldMedalCnt + " \n silver = " + silverMedalCnt + "\n black = " + blackMedalCnt + "\n totalPoint = " + totalPoint);


                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {

                }
            });
        }
        return binding.getRoot();

    }

    private void setMaxTextureSize() {

        EGLDisplay dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY);
        int[] vers = new int[2];
        EGL14.eglInitialize(dpy, vers, 0, vers, 1);
        int[] configAttr = {
                EGL14.EGL_COLOR_BUFFER_TYPE, EGL14.EGL_RGB_BUFFER,
                EGL14.EGL_LEVEL, 0,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                EGL14.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfig = new int[1];
        EGL14.eglChooseConfig(dpy, configAttr, 0,
                configs, 0, 1, numConfig, 0);
        // TROUBLE! No config found.
        EGLConfig config = configs[0];
        int[] surfAttr = {
                EGL14.EGL_WIDTH, 64,
                EGL14.EGL_HEIGHT, 64,
                EGL14.EGL_NONE
        };
        EGLSurface surf = EGL14.eglCreatePbufferSurface(dpy, config, surfAttr, 0);

        int[] ctxAttrib = {
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE
        };
        EGLContext ctx = EGL14.eglCreateContext(dpy, config, EGL14.EGL_NO_CONTEXT, ctxAttrib, 0);

        EGL14.eglMakeCurrent(dpy, surf, surf, ctx);

        int[] maxSize = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0);

        int maxTextureSize = maxSize[0];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.motive_leaderboard_img, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        String imageType = options.outMimeType;
        Log.d("imageHeight", imageHeight + " imageWidth:" + imageWidth + " imageType:" + imageType);
        Log.d("디바이스 이미지 최대 크기", " " + maxTextureSize);

        if (imageHeight < maxTextureSize && imageWidth < maxTextureSize) {
            binding.mapImage.setImageResource(R.drawable.motive_leaderboard_img);
        } else {
            double scaling;
            if (imageHeight >= imageWidth) {
                scaling = (double) imageHeight / (maxTextureSize - 100);
            } else {
                scaling = (double) imageWidth / (maxTextureSize - 100);
            }
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.motive_leaderboard_img);
            Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (imageWidth / scaling), (int) (imageHeight / scaling), true);
            binding.mapImage.setImageBitmap(resized);
        }

        EGL14.eglMakeCurrent(dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE,
                EGL14.EGL_NO_CONTEXT);
        EGL14.eglDestroySurface(dpy, surf);
        EGL14.eglDestroyContext(dpy, ctx);
        EGL14.eglTerminate(dpy);
    }

    private void setMedal() {
        setMaxTextureSize();
        binding.medalMapScrollView.post(() -> {
            Log.d("medalResultGetY", " " + binding.medalResultLayout.getHeight());
            DisplayMetrics dm = Objects.requireNonNull(getActivity()).getApplicationContext().getResources().getDisplayMetrics();
            int deviceHeight = dm.heightPixels;
            int[] xValues = {48, 24, 12, 20, 60, 84, 84, 60, 24, 12, 36, 68, 92, 68, 36, 12, 36, 68, 92, 68, 36, 24, 48};
            double[] yValues = {791.7, 767.7, 737.7, 707.7, 677.7, 653.7, 623.7, 599.7, 575.7, 545.7, 521.7, 509.7, 461.7, 437.7, 413.7, 383.7, 335.7, 322.0, 292.0, 262.0, 238.0, 208.0, 178.0};
            for (int i = 0; i < 23; i++) {
                mapHeight = binding.mapImage.getBottom();
                mapWidth = binding.mapImage.getWidth();
                sizeValueX = xValues[0] - binding.centerValue.getX() / mapWidth * 1.2 * 100;
                sizeValueY = 412 - binding.imageTest.getY() / mapHeight * 8.48 * 100;
                float setValueX = (float) (mapWidth * ((xValues[i] - sizeValueX) / 1.2 / 100));
                float setValueY = (float) (mapHeight * ((yValues[i] - sizeValueY) / 8.48 / 100));
                MedalLayout medalLayout = new MedalLayout(getContext());
                switch (weekCount) {
                    case 4:
                        if (i == 19) {
                            medalLayout.setMedalTextVisibility(false);
                        }
                        if (i > 19) {
                            binding.lastMedal.setText("4");
                            medalLayout.setWeekCount(Integer.toString(i - 19));
                            if (medalInfoVOS.size() > i - 20) {
                                if (medalInfoVOS.get(i - 20).getGoldMedalCnt() == 1) {
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_g);
                                } else if (medalInfoVOS.get(i - 20).getSilverMedalCnt() == 1) {
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_s);
                                } else if (i!=20&&i!=20+currentCnt-1){
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_b);
                                }
                                setValueX = (float) (mapWidth * ((xValues[i] - sizeValueX - 6) / 1.2 / 100));
                            }
                            binding.fogImage1.setVisibility(View.VISIBLE);
                            fogHeight = binding.fogImage1.getHeight();
                            scrollValue = (int) (mapHeight - fogHeight - deviceHeight + 600);
                            duration = 1000;
                            Log.d("mapHeight", mapHeight + " ");
                            Log.d("deviceHeight", deviceHeight + " ");
                            Log.d("fogHeight1", fogHeight + " ");
                            if ((i + 1) % 4 == 0) {
                                medalLayout.setTextBack(R.drawable.medal_text_back_green);
                                medalLayout.setTextColor(Color.WHITE);
                            } else {
                                medalLayout.setTextBack(R.drawable.medal_text_back_white);
                            }
                        }
                        break;
                    case 8:
                        if (i == 15) {
                            medalLayout.setMedalTextVisibility(false);
                        }
                        if (i > 15) {
                            binding.lastMedal.setText("8");
                            medalLayout.setWeekCount(Integer.toString(i - 15));
                            if (medalInfoVOS.size() > i - 16) {
                                if (medalInfoVOS.get(i - 16).getGoldMedalCnt() == 1) {
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_g);
                                } else if (medalInfoVOS.get(i - 16).getSilverMedalCnt() == 1) {
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_s);
                                } else if (i!=16&&i!=16+currentCnt-1){
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_b);
                                }
                                setValueX = (float) (mapWidth * ((xValues[i] - sizeValueX - 6) / 1.2 / 100));
                            }
                            binding.fogImage2.setVisibility(View.VISIBLE);
                            fogHeight = binding.fogImage2.getHeight();
                            duration = 1250;
                            scrollValue = (int) (mapHeight - fogHeight - deviceHeight+600);
                            Log.d("mapHeight", mapHeight + " ");
                            Log.d("deviceHeight", deviceHeight + " ");
                            Log.d("fogHeight2", fogHeight + " ");
                            if ((i + 1) % 4 == 0) {
                                medalLayout.setTextBack(R.drawable.medal_text_back_green);
                                medalLayout.setTextColor(Color.WHITE);
                            } else {
                                medalLayout.setTextBack(R.drawable.medal_text_back_white);
                            }
                        }
                        break;
                    case 12:
                        if (i == 11) {
                            medalLayout.setMedalTextVisibility(false);
                        }
                        if (i > 11) {
                            binding.lastMedal.setText("12");
                            medalLayout.setWeekCount(Integer.toString(i - 11));
                            if (medalInfoVOS.size() > i - 12) {
                                if (medalInfoVOS.get(i - 12).getGoldMedalCnt() == 1) {
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_g);
                                } else if (medalInfoVOS.get(i - 12).getSilverMedalCnt() == 1) {
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_s);
                                } else if (i!=12&&i!=12+currentCnt-1){
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_b);
                                }
                                setValueX = (float) (mapWidth * ((xValues[i] - sizeValueX - 6) / 1.2 / 100));
                            }
                            binding.fogImage3.setVisibility(View.VISIBLE);
                            fogHeight = binding.fogImage3.getHeight();
                            duration = 1500;
                            scrollValue = mapHeight - binding.fogImage3.getHeight() - deviceHeight + 600;
                            Log.d("mapHeight", mapHeight + " ");
                            Log.d("fogHeight3", fogHeight + " ");
                            if ((i + 1) % 4 == 0) {
                                medalLayout.setTextBack(R.drawable.medal_text_back_green);
                                medalLayout.setTextColor(Color.WHITE);
                            } else {
                                medalLayout.setTextBack(R.drawable.medal_text_back_white);
                            }
                        }
                        break;
                    case 16:
                        if (i == 7) {
                            medalLayout.setMedalTextVisibility(false);
                        }
                        if (i > 7) {
                            binding.lastMedal.setText("16");
                            medalLayout.setWeekCount(Integer.toString(i - 7));
                            if (medalInfoVOS.size() > i - 8) {
                                if (medalInfoVOS.get(i - 8).getGoldMedalCnt() == 1) {
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_g);
                                } else if (medalInfoVOS.get(i - 8).getSilverMedalCnt() == 1) {
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_s);
                                } else if (i!=8&&i!=8+currentCnt-1){
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_b);
                                }
                                setValueX = (float) (mapWidth * ((xValues[i] - sizeValueX - 6) / 1.2 / 100));
                            }
                            binding.fogImage4.setVisibility(View.VISIBLE);
                            fogHeight = binding.fogImage4.getHeight();
                            duration = 1750;
                            scrollValue = mapHeight - binding.fogImage4.getHeight() - deviceHeight + 600;
                            Log.d("mapHeight", mapHeight + " ");
                            Log.d("fogHeight3", fogHeight + " ");
                            if ((i + 1) % 4 == 0) {
                                medalLayout.setTextBack(R.drawable.medal_text_back_green);
                                medalLayout.setTextColor(Color.WHITE);
                            } else {
                                medalLayout.setTextBack(R.drawable.medal_text_back_white);
                            }
                        }
                        break;
                    case 20:
                        if (i == 3) {
                            medalLayout.setMedalTextVisibility(false);
                        }
                        if (i > 3) {
                            binding.lastMedal.setText("20");
                            medalLayout.setWeekCount(Integer.toString(i - 3));
                            if (medalInfoVOS.size() > i - 4) {
                                if (medalInfoVOS.get(i - 4).getGoldMedalCnt() == 1) {
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_g);
                                } else if (medalInfoVOS.get(i - 4).getSilverMedalCnt() == 1) {
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_s);
                                } else if (i!=4&&i!=4+currentCnt-1){
                                    medalLayout.setMedal(R.drawable.motive_img_013_medal_b);
                                }
                                setValueX = (float) (mapWidth * ((xValues[i] - sizeValueX - 6) / 1.2 / 100));
                            }
                            binding.fogImage5.setVisibility(View.VISIBLE);
                            fogHeight = binding.fogImage5.getHeight();
                            duration = 2000;
                            scrollValue = mapHeight - binding.fogImage5.getHeight() - deviceHeight + 600;
                            Log.d("mapHeight", mapHeight + " ");
                            Log.d("fogHeight3", fogHeight + " ");
                            if ((i + 1) % 4 == 0) {
                                medalLayout.setTextBack(R.drawable.medal_text_back_green);
                                medalLayout.setTextColor(Color.WHITE);
                            } else {
                                medalLayout.setTextBack(R.drawable.medal_text_back_white);
                            }
                        }
                        break;
                    case 24:
                        binding.lastMedal.setText("24");
                        scrollValue = binding.mapImage.getBottom();
                        medalLayout.setWeekCount(Integer.toString(i + 1));
                        if (medalInfoVOS.size() > i) {
                            if (medalInfoVOS.get(i).getGoldMedalCnt() == 1) {
                                medalLayout.setMedal(R.drawable.motive_img_013_medal_g);
                            } else if (medalInfoVOS.get(i).getSilverMedalCnt() == 1) {
                                medalLayout.setMedal(R.drawable.motive_img_013_medal_s);
                            } else if (i!=0&&i!=currentCnt-1){
                                medalLayout.setMedal(R.drawable.motive_img_013_medal_b);
                            }
                            setValueX = (float) (mapWidth * ((xValues[i] - sizeValueX - 6) / 1.2 / 100));
                            if (i == 3) {
                                setValueX = (float) (mapWidth * ((xValues[i] - sizeValueX - 2) / 1.2 / 100));
                            }
                        }
                        if ((i + 1) % 4 == 0) {
                            medalLayout.setTextBack(R.drawable.medal_text_back_green);
                            medalLayout.setTextColor(Color.WHITE);
                        } else {
                            medalLayout.setTextBack(R.drawable.medal_text_back_white);
                        }
                        break;
                }
                if (medalInfoVOS.size() == 24) {
                    if (medalInfoVOS.get(23).getGoldMedalCnt() == 1) {
                        binding.lastMedalImg.setImageResource(R.drawable.motive_img_013_medal_g);
                    } else if (medalInfoVOS.get(23).getSilverMedalCnt() == 1) {
                        binding.lastMedalImg.setImageResource(R.drawable.motive_img_013_medal_s);
                    } else{
                        binding.lastMedalImg.setImageResource(R.drawable.motive_img_013_medal_b);
                    }
                }
                medalLayout.setXY(setValueX, setValueY);
                binding.myMedalLayout.addView(medalLayout);
            }
            scrollLimit = mapHeight - fogHeight - deviceHeight + 1000;
            ObjectAnimator.ofInt(binding.medalMapScrollView, "scrollY", scrollValue).setDuration(duration).start();
        });
    }

    private int currentWeekCount(String start) throws Exception {
        Calendar calendar = Calendar.getInstance();
        String[] starts = start.split("-");
        calendar.set(Integer.parseInt(starts[0]), Integer.parseInt(starts[1]) - 1, Integer.parseInt(starts[2]));
        int startDayNum = calendar.get(Calendar.DAY_OF_WEEK);
        Log.e(TAG, "startDayNum = " + startDayNum);

        Date endDate = new Date();

        String today = simpleDateFormat.format(endDate);
        String[] todays = today.split("-");
        calendar.set(Integer.parseInt(todays[0]), Integer.parseInt(todays[1]) - 1, Integer.parseInt(todays[2]));
        int todayNum = calendar.get(Calendar.DAY_OF_WEEK);
        Log.e(TAG, "todayNum = " + todayNum);

        Date startDate = simpleDateFormat.parse(start);
        long diff = endDate.getTime() - startDate.getTime();
        int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
        diffDays = ((diffDays - todayNum + startDayNum) / 7) + 1;
        Log.e(TAG, "diff days = " + diffDays);
        return diffDays;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
