package com.instantrip.presentation.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.slidingpanelayout.widget.SlidingPaneLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.instantrip.R
import com.instantrip.databinding.ActivityMessageBinding
import com.instantrip.databinding.CommentWriteDialogBinding
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener

class MessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMessageBinding

    //메세지 카드뷰
    private lateinit var messageCardData: ArrayList<String>
    private lateinit var messageListAdapter: MessageListAdapter
    private var messagePosition: Int = 0

    //댓글 카드뷰
    private lateinit var commentList: RecyclerView
    private lateinit var commentListAdapter: RecyclerView.Adapter<*>
    private lateinit var commentListManager: RecyclerView.LayoutManager
    private var isCommentOpen: Boolean = false

    //댓글 입력 다이얼로그
    private lateinit var commentWriteLayout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadMessageCards()

        loadCommentWriteDialog()
    }

    //메세지 카드를 만들어서 뷰페이저에 연결
    private fun loadMessageCards(){
        //init list
        messageCardData = ArrayList()

        //add items/data to list
        messageCardData.add("안녕하세요1")
        messageCardData.add("안녕하세요2")
        messageCardData.add("안녕하세요3")
        messageCardData.add("안녕하세요4")

        //setup adapter
        messageListAdapter = MessageListAdapter(this, messageCardData)

        //set adapter to viewPager
        binding.messageListViewpager.adapter = messageListAdapter

        //set default padding
        binding.messageListViewpager.setPadding(100, 0, 100, 0)

        //set default indicator "1/count"
        var indicatorText = "1/"+messageListAdapter.count
        binding.messageListIndicator.setText(indicatorText)

        //set indicator listener
        binding.messageListViewpager.addOnPageChangeListener(object: ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                //
            }

            override fun onPageSelected(position: Int) {
                var indicatorText = ""+(position+1)+"/"+messageListAdapter.count
                binding.messageListIndicator.setText(indicatorText)

                messagePosition = position
            }
        })

        //set slidingup panel listener
        binding.messageMain.addPanelSlideListener(object: SlidingUpPanelLayout.PanelSlideListener{
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
            }

            override fun onPanelStateChanged(
                panel: View?,
                previousState: SlidingUpPanelLayout.PanelState?,
                newState: SlidingUpPanelLayout.PanelState?
            ) {
                //닫힘 - 초기화
                if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    isCommentOpen = false
                    binding.commentContent.removeAllViews()
                }
                //열림 - 데이터 세팅
                else if(newState == SlidingUpPanelLayout.PanelState.EXPANDED){
                    isCommentOpen = true
                    var commentCardData: ArrayList<String> = ArrayList()

                    commentCardData.add("안녕하세요-기본")
                    commentCardData.add(messageCardData[messagePosition])

                    loadCommentCards(commentCardData)
                }
            }

        })
    }


    //댓글 카드를 만들어서 리사이클러뷰에 연결
    private fun loadCommentCards(commentCardData: ArrayList<String>){
        commentListManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        commentListAdapter = CommentListAdapter(commentCardData)

        commentList = findViewById<RecyclerView>(R.id.comment_content).apply {
            setHasFixedSize(true)
            layoutManager = commentListManager
            adapter = commentListAdapter
        }
    }

    //댓글작성 버튼에 다이얼로그 연결
    private fun loadCommentWriteDialog(){
        commentWriteLayout = LayoutInflater.from(this).inflate(R.layout.comment_write_dialog, null, false)
        var commentWriteDialog = BottomSheetDialog(this)
        commentWriteDialog.setContentView(commentWriteLayout)

        binding.commentButton.setOnClickListener {
            commentWriteDialog.show()
        }

        var commentWriteButton = commentWriteLayout.findViewById<Button>(R.id.comment_write_button)
        commentWriteButton.setOnClickListener {
            Toast.makeText(this, ""+(messagePosition+1)+"번째 글에 댓글남김", Toast.LENGTH_SHORT).show()
            commentWriteDialog.hide()
        }

    }

    //뒤로가기 애니메이션 출력. 추후 닫기 버튼에도 연결
    private fun goBack(){
        binding.messageLayout.setTransitionListener(object: MotionLayout.TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                binding.messageLayout.scene.disableAutoTransition(true);
                finish()
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }

        })

        binding.messageLayout.transitionToStart()

        binding.commentLayout.setTransitionListener(object: MotionLayout.TransitionListener{
            override fun onTransitionStarted(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int
            ) {
            }

            override fun onTransitionChange(
                motionLayout: MotionLayout?,
                startId: Int,
                endId: Int,
                progress: Float
            ) {
            }

            override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
                binding.commentLayout.scene.disableAutoTransition(true);
                finish()
            }

            override fun onTransitionTrigger(
                motionLayout: MotionLayout?,
                triggerId: Int,
                positive: Boolean,
                progress: Float
            ) {
            }

        })

        binding.commentLayout.transitionToStart()
    }

    //뒤로가기버튼 클릭시
    override fun onBackPressed() {
        if(isCommentOpen){
            binding.messageMain.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }else{
            goBack()
        }
    }
}
