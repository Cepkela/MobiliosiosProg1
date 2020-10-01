package viko.eif.lt.mobiliosiosprog1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import kotlinx.android.synthetic.main.activity_main.*
import viko.eif.lt.mobiliosiosprog1.bottomNavigationFragments.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private val MY_REQUEST_CODE: Int = 7117
    lateinit var providers : List<AuthUI.IdpConfig>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val homeFragment = HomeFragment()
        val ratingFragment = RatingFragment()
        val moviesFragment = MoviesFragment()
        val moreFragment = MoreFragment()
        val helpFragment = HelpFragment()

        makeCurrentFragment(homeFragment)
        bottom_navigation.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.ic_home -> makeCurrentFragment(homeFragment)
                R.id.ic_star -> makeCurrentFragment(ratingFragment)
                R.id.ic_movies -> makeCurrentFragment(moviesFragment)
                R.id.ic_more -> makeCurrentFragment(moreFragment)
                R.id.ic_help -> makeCurrentFragment(helpFragment)
            }
            true
        }

        //Init
        providers = Arrays.asList<AuthUI.IdpConfig>(AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
            )

        showSignInOptions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==MY_REQUEST_CODE)
        {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode==Activity.RESULT_OK)
            {
                val user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(this,""+user!!.email, Toast.LENGTH_SHORT).show()

                //btn_sign_out.isEnabled = true
            }
            else
            {
                Toast.makeText(this,""+response!!.error!!.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSignInOptions(){
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.MyTheme)
            .build(), MY_REQUEST_CODE)
    }

    private fun makeCurrentFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val settingsFragment = fragment_settings();
        return when (item.itemId){
            R.id.signOut ->{
                AuthUI.getInstance().signOut(this@MainActivity)
                    .addOnCompleteListener { //btn_sign_out.isEnabled = false
                        showSignInOptions()
                    }
                    .addOnFailureListener { e-> Toast.makeText(this@MainActivity,e.message, Toast.LENGTH_LONG).show() }
                true
            }
            R.id.settings ->{
               makeCurrentFragment(settingsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.top_menu, menu)
        return true
    }

}