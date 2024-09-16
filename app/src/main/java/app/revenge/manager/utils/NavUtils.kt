package app.revenge.manager.utils

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

tailrec fun Navigator.navigate(screen: Screen) {
    if (level == 0)
        push(screen)
    else
        this.parent!!.navigate(screen)
}