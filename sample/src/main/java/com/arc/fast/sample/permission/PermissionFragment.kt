package com.arc.fast.sample.permission

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.arc.fast.permission.FastPermissionRequest
import com.arc.fast.permission.FastPermissionResult
import com.arc.fast.permission.FastPermissionUtil
import com.arc.fast.sample.BaseFragment
import com.arc.fast.sample.databinding.FragmentPermissionBinding
import com.arc.fast.sample.databinding.FragmentViewBinding

class PermissionFragment : BaseFragment<FragmentPermissionBinding>() {

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentPermissionBinding = FragmentPermissionBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.apply {
            this.navigationIcon = navigationIconForBack
            this.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
        binding.tvGet.setOnClickListener {
            FastPermissionUtil.request(
                this, // or fragment
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
            ) { allGranted: Boolean, result: Map<String, FastPermissionResult> ->
                // allGranted：是否全部权限获取成功
                // result：各个权限的获取结果，key为permission，value为获取结果（Granted:同意；Denied:拒绝；DeniedAndDonTAskAgain:拒绝且不再询问）
                if (allGranted) {
                    // 全部权限获取成功
                    Toast.makeText(context, "全部权限获取成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "权限被拒绝", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.tvGet2.setOnClickListener {
            FastPermissionUtil.request(
                fragment = this,
                FastPermissionRequest(Manifest.permission.CAMERA, "应用需要相机权限用于扫描"),
                FastPermissionRequest(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    "应用需要储存权限用于选择扫描图片"
                ),
            ) { allGranted, result ->
                if (allGranted) {
                    // 全部权限获取成功
                    Toast.makeText(context, "全部权限获取成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "权限被拒绝", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}