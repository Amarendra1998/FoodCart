package com.example.admin.foodcart;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.admin.foodcart.Common.Common;
import com.example.admin.foodcart.Database.Database;
import com.example.admin.foodcart.Interface.ItemClickListener;
import com.example.admin.foodcart.Model.Banner;
import com.example.admin.foodcart.Model.Category;
import com.example.admin.foodcart.Model.Token;
import com.example.admin.foodcart.ViewHolder.MenuViewHolder;
import com.facebook.Profile;
import com.facebook.accountkit.AccountKit;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        private FirebaseDatabase database;
        private DatabaseReference category;
        private TextView textfullname;
       private RecyclerView recyclerView;
        CounterFab fab;
       private RecyclerView.LayoutManager layoutManager;
       private SwipeRefreshLayout  swipeRefreshLayout;
       HashMap<String,String>image_list;
       SliderLayout mSlider;
    private FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);


        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category,Category.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, final int position, @NonNull Category model) {
                viewHolder.textmenuname.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                final Category clickitem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int Posiition, boolean islongclick) {
                        Intent foddlist = new Intent(Home.this,FoodList.class);
                        foddlist.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(foddlist);
                        Toast.makeText(Home.this,""+clickitem.getName(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @NonNull
            @Override
            public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View itemview = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.menu_item,viewGroup,false);
                return new MenuViewHolder(itemview);
            }
        };
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
               if (Common.isConnectedToInternet(getBaseContext()))
                    loadmenu();
                else
                {
                    Toast.makeText(getBaseContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                if (Common.isConnectedToInternet(getBaseContext()))
                    loadmenu();
                else
                {
                    Toast.makeText(getBaseContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        Paper.init(this);
         fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myint = new Intent(Home.this,Cart.class);
                startActivity(myint);
            }
        });
        fab.setCount(new Database(this).getCartCount(Common.currentUser.getPhone()));
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview= navigationView.getHeaderView(0);
        textfullname = (TextView)headerview.findViewById(R.id.textfullname);
        textfullname.setText(Common.currentUser.getName());

        recyclerView = (RecyclerView)findViewById(R.id.recycler_menu);
       // recyclerView.setHasFixedSize(true);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recyclerView.getContext(),R.anim.layout_fall_down);
        recyclerView.setLayoutAnimation(controller);
        //layoutManager = new LinearLayoutManager(this);
       // recyclerView.setLayoutManager(layoutManager);

        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        updateToken(FirebaseInstanceId.getInstance().getToken());

        setupslider();

    }

    private void setupslider() {
        mSlider = (SliderLayout)findViewById(R.id.slider);
        image_list=new HashMap<>();
        final DatabaseReference banners = database.getReference("Banner");
        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
               {
                   Banner banner = postSnapshot.getValue(Banner.class);
                   image_list.put(banner.getName()+"@@@"+banner.getId(),banner.getImage());
               }
               for (String key:image_list.keySet())
               {
                   String[] keySplit = key.split("@@@");
                   String nameOfFood = keySplit[0];
                   String idOfFood = keySplit[1];
                   final TextSliderView textSliderView = new TextSliderView(getBaseContext());
                   textSliderView.description(nameOfFood)
                           .image(image_list.get(key))
                           .setScaleType(BaseSliderView.ScaleType.Fit)
                           .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                               @Override
                               public void onSliderClick(BaseSliderView slider) {
                                   Intent intent = new Intent(Home.this,FoodDetail.class);
                                   intent.putExtras(textSliderView.getBundle());
                                   startActivity(intent);
                               }
                           });

                   textSliderView.bundle(new Bundle());
                   textSliderView.getBundle().putString("FoodId",idOfFood);

                   mSlider.addSlider(textSliderView);
                   banners.removeEventListener(this);
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);
    }

    private void updateToken(String token) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference tokens = db.getReference("Tokens");
        Token data = new Token(token,false);
        tokens.child(Common.currentUser.getPhone()).setValue(data);
    }

    private void loadmenu() {
       /* FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(category,Category.class)
                .build();
         adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(options) {
             @Override
             protected void onBindViewHolder(@NonNull MenuViewHolder viewHolder, final int position, @NonNull Category model) {
                 viewHolder.textmenuname.setText(model.getName());
                 Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.imageView);
                 final Category clickitem = model;
                 viewHolder.setItemClickListener(new ItemClickListener() {
                     @Override
                     public void onClick(View view, int Posiition, boolean islongclick) {
                         Intent foddlist = new Intent(Home.this,FoodList.class);
                         foddlist.putExtra("CategoryId",adapter.getRef(position).getKey());
                         startActivity(foddlist);
                         Toast.makeText(Home.this,""+clickitem.getName(),Toast.LENGTH_SHORT).show();
                     }
                 });
             }

             @NonNull
             @Override
             public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                 View itemview = LayoutInflater.from(viewGroup.getContext())
                         .inflate(R.layout.menu_item,viewGroup,false);
                 return new MenuViewHolder(itemview);
             }
         };*/
         adapter.startListening();
         recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        mSlider.stopAutoCycle();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCartCount(Common.currentUser.getPhone()));
         if (adapter!=null){
             adapter.startListening();
         }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {
           startActivity(new Intent(Home.this,SearchActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            // Handle the camera action
        } else if (id == R.id.nav_cart) {
            Intent cartintent = new Intent(Home.this,Cart.class);
            startActivity(cartintent);
        } else if (id == R.id.nav_morder) {
            Intent orderintent = new Intent(Home.this,OrderStatus.class);
            startActivity(orderintent);
        } else if (id == R.id.log_out) {
            AccountKit.logOut();
            //Paper.book().destroy();
             Intent signIntent = new Intent(Home.this,Main2Activity.class);
             signIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
             startActivity(signIntent);
        }else if (id==R.id.nav_change_name){
            showChangePasswordDialog();
        }else if (id==R.id.nav_home){
            showHomeAddressDialog();
        }else if (id==R.id.nav_settings){
            showSettingDialog();
        }else if (id==R.id.nav_favorites){
            Intent intent = new Intent(Home.this,FavoritesActivity.class);
            startActivity(intent);
        }else if (id==R.id.nav_profile){
            Intent intent = new Intent(Home.this,ProfileActivity.class);
            startActivity(intent);
        }else if (id==R.id.nav_contact){
            Intent intent = new Intent(Home.this,ContactUs.class);
            startActivity(intent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showSettingDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Settings");
        LayoutInflater inflater =  LayoutInflater.from(this);
        View layout_settings= inflater.inflate(R.layout.settings_layout,null);
        final CheckBox ckb_subscribe_new = (CheckBox)layout_settings.findViewById(R.id.ckb_sub_new);
        Paper.init(this);
        String isSubscribe = Paper.book().read("sub_new");
        if (isSubscribe==null || TextUtils.isEmpty(isSubscribe)|| isSubscribe.equals("false"))
            ckb_subscribe_new.setChecked(false);
        else
            ckb_subscribe_new.setChecked(true);
        alertDialog.setView(layout_settings);
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (ckb_subscribe_new.isChecked())
                {
                    FirebaseMessaging.getInstance().subscribeToTopic(Common.topicName);
                    Paper.book().write("sub_new","true");
                }
                else
                {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Common.topicName);
                    Paper.book().write("sub_new","false");
                }
            }
        });
        alertDialog.show();
    }

    private void showHomeAddressDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Change Home Address");
        alertDialog.setMessage("Please fill all information");
        LayoutInflater inflater =  LayoutInflater.from(this);
        View layout_hom= inflater.inflate(R.layout.home_address_layout,null);
        final MaterialEditText edtHomeAddress = (MaterialEditText)layout_hom.findViewById(R.id.edtHomeAddress);
        alertDialog.setView(layout_hom);
        alertDialog.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Common.currentUser.setHomeAddress(edtHomeAddress.getText().toString());
                FirebaseDatabase.getInstance().getReference("User")
                        .child(Common.currentUser.getPhone())
                        .setValue(Common.currentUser)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(Home.this,"Address Updated Successfully",Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        alertDialog.show();
    }

    private void showChangePasswordDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Home.this);
        alertDialog.setTitle("Update Name");
        alertDialog.setMessage("Please fill all information");
        LayoutInflater inflater =  LayoutInflater.from(this);
        View layout_name= inflater.inflate(R.layout.update_name_layout,null);
        final MaterialEditText edtName = (MaterialEditText)layout_name.findViewById(R.id.edtName);

        alertDialog.setView(layout_name);
        alertDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               final android.app.AlertDialog waitingDialog = new SpotsDialog(Home.this);
               waitingDialog.show();
                 Map<String,Object>update_name = new HashMap<>();
                 update_name.put("name",edtName.getText().toString());

                  FirebaseDatabase.getInstance().getReference("User").child(Common.currentUser.getPhone())
                         .updateChildren(update_name)
                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 waitingDialog.dismiss();
                                 if (task.isSuccessful())
                                 {
                                     Toast.makeText(Home.this," Name was Updated",Toast.LENGTH_SHORT).show();
                                 }
                             }
                         });
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                   dialog.dismiss();
            }
        });
        alertDialog.show();
    }
}
