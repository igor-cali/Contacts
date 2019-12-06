package com.simplemobiletools.contacts.pro.activities

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import androidx.viewpager.widget.ViewPager
import com.simplemobiletools.commons.extensions.*
import com.simplemobiletools.commons.helpers.PERMISSION_GET_ACCOUNTS
import com.simplemobiletools.commons.helpers.PERMISSION_READ_CONTACTS
import com.simplemobiletools.commons.helpers.PERMISSION_WRITE_CONTACTS
import com.simplemobiletools.contacts.pro.R
import com.simplemobiletools.contacts.pro.extensions.config
import com.simplemobiletools.contacts.pro.helpers.CONTACTS_TAB_MASK
import com.simplemobiletools.contacts.pro.helpers.FAVORITES_TAB_MASK
import kotlinx.android.synthetic.main.activity_insert_edit_contact.*

class InsertOrEditContactActivity : SimpleActivity() {
    private val START_INSERT_ACTIVITY = 1
    private val START_EDIT_ACTIVITY = 2

    private val contactsFavoritesList = arrayListOf(CONTACTS_TAB_MASK,
            FAVORITES_TAB_MASK
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_edit_contact)

        if (checkAppSideloading()) {
            return
        }

        setupTabColors()

        // we do not really care about the permission request result. Even if it was denied, load private contacts
        handlePermission(PERMISSION_READ_CONTACTS) {
            if (it) {
                handlePermission(PERMISSION_WRITE_CONTACTS) {
                    handlePermission(PERMISSION_GET_ACCOUNTS) {
                        initFragments()
                    }
                }
            } else {
                initFragments()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        updateMenuItemColors(menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun initFragments() {
        viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                insert_or_edit_tabs_holder.getTabAt(position)?.select()
                invalidateOptionsMenu()
            }
        })

        insert_or_edit_tabs_holder.onTabSelectionChanged(
                tabUnselectedAction = {
                    it.icon?.applyColorFilter(config.textColor)
                },
                tabSelectedAction = {
                    viewpager.currentItem = it.position
                    it.icon?.applyColorFilter(getAdjustedPrimaryColor())
                }
        )

        insert_or_edit_tabs_holder.removeAllTabs()
        var skippedTabs = 0
        contactsFavoritesList.forEachIndexed { index, value ->
            if (config.showTabs and value == 0) {
                skippedTabs++
            } else {
                val tab = insert_or_edit_tabs_holder.newTab().setIcon(getTabIcon(index))
                insert_or_edit_tabs_holder.addTab(tab, index - skippedTabs, index == 0)
            }
        }

        insert_or_edit_tabs_holder.beVisibleIf(skippedTabs == 0)
    }

    private fun setupTabColors() {
        insert_or_edit_tabs_holder.apply {
            background = ColorDrawable(config.backgroundColor)
            setSelectedTabIndicatorColor(getAdjustedPrimaryColor())
        }
    }
}
