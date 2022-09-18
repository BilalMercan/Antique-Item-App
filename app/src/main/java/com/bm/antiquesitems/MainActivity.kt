package com.bm.antiquesitems

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.bm.antiquesitems.databinding.ActivityMainBinding
import java.lang.Exception
import androidx.recyclerview.widget.LinearLayoutManager


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var antiqueList : ArrayList<Antique>
    private lateinit var itemAdaptor : ItemAdaptor

            override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val views = binding.root
        setContentView(views)

                antiqueList = ArrayList<Antique>()
                itemAdaptor = ItemAdaptor(antiqueList)
                binding.recyclerView.layoutManager = LinearLayoutManager(this)
                binding.recyclerView.adapter = itemAdaptor
                try {
                    val database = this.openOrCreateDatabase("Artinfos", MODE_PRIVATE,null)
                    val cursor = database.rawQuery("SELECT * FROM artinfostable",null)
                    val antiquenameIx = cursor.getColumnIndex("name")
                    val idIx = cursor.getColumnIndex("id")

                    while (cursor.moveToNext()){
                        val name = cursor.getString(antiquenameIx)
                        val id = cursor.getInt(idIx)
                        val art = Antique(name, id)
                        antiqueList.add(art)

                    }

                    itemAdaptor.notifyDataSetChanged()

                    cursor.close()

                }
                catch (e: Exception){


                }




            }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.detail_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.Add_Antique){
             val intent = Intent(this@MainActivity, ItemsDetail::class.java)
            intent.putExtra("info","new")
            startActivity(intent)

        }

        return super.onOptionsItemSelected(item)
    }












}