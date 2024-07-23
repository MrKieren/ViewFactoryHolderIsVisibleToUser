package com.android.bugs.viewfactoryholderisvisibletouser

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class SimpleAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            if (event?.packageName != packageName) {
                return
            }

            val rootNode = rootInActiveWindow

            if (rootNode != null) {
                findNodeByClassName(rootNode, VIEW_FACTORY_HOLDER_CLASS_NAME)?.let { viewFactoryHolder ->
                    Log.i(TAG, "Found target node. Is visible to user: ${viewFactoryHolder.isVisibleToUser}")
                }
            }
        }
    }

    private fun findNodeByClassName(
        rootNode: AccessibilityNodeInfo,
        className: String
    ): AccessibilityNodeInfo? {
        val nodes = ArrayDeque<AccessibilityNodeInfo>()
        nodes.add(rootNode)

        while (nodes.isNotEmpty()) {
            val node = nodes.removeFirst()
            node?.let {
                if (it.className != null && it.className.equals(className)) {
                    return it
                }

                for (i in 0 until it.childCount) {
                    val child = it.getChild(i)
                    if (child != null) {
                        nodes.add(child)
                    }
                }
            }
        }

        return null
    }

    override fun onInterrupt() {
        // Not used.
    }

    companion object {
        private const val TAG = "SimpleAccessibilityService"
        const val VIEW_FACTORY_HOLDER_CLASS_NAME = "androidx.compose.ui.viewinterop.ViewFactoryHolder"
    }
}