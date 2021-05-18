# AnimationX
使用Flow封装了一套api，提供了一种简单的方法来在创建视图动画。 


<img src="https://github.com/iotxc/AnimationX/blob/main/screen/demo.gif" height="550" alt="图片加载失败时，显示这段字"/>

# 用法
## alpha :透明度
```
view.run {
    launch(this.alpha(ANIMATION_DURATION, reverse = true))
}
```

```
rxLifeScope.launch {
    view.alpha(ANIMATION_DURATION, reverse = true).collect()
}
```

```
//记得在view销毁时取消， 不推荐
val job = GlobalScope.launch {
    view.alpha(ANIMATION_DURATION, reverse = true).collect()
}

job.cancel()
```

## translation:平移
```
view.run {
    launch(this.translation(500f, 500f, ANIMATION_DURATION,
     reverse = true))
}
```

## scale:缩放
```
view.run {
    launch(this.scale(0f, ANIMATION_DURATION, reverse = true))
}
```

## backgroundColor：背景色
```
view.run {
    launch(this.backgroundColor(
        ContextCompat.getColor(this@MainActivity, R.color.primary),
        ContextCompat.getColor(this@MainActivity, R.color.accent),
        ANIMATION_DURATION, reverse = true
    ))
}
```

## rotation：旋转
```
view.run {
    launch(this.rotation(360f, ANIMATION_DURATION))
}
```

## rangeAnyToCompletable:
```
view.run {
    launch(this.(0f to 30f).rangeAnyToCompletable(ANIMATION_DURATION) {
        view.text = format.format(it as Float)
    })
}
```

## reveal:揭示动画
```
view.run {
    launch(this.reveal(
        viewX.toInt(),
        viewY.toInt(),
        0f,
        hypot(
            this.width.toDouble(),
            this.height.toDouble()
        ).toFloat(),
        2000L
    ))
}
```

## 多个动画同时进行
```
AnimationX.together(
    binding.fab.rotation(360f, ANIMATION_DURATION),
    binding.text.fadeIn(ANIMATION_DURATION)
).collect()
```

## 多个动画顺序进行
```
AnimationX.sequentially(
    binding.cardView.scale(1f, ANIMATION_DURATION),
    binding.fab.scale(1f, ANIMATION_DURATION),
    binding.progressBar.fadeIn(ANIMATION_DURATION)
).collect()
```

## 动画结束后执行操作
```
view.run {
    launch(this.fadeOut().onCompletion { 
        //do something
    })
}
```

# 智能功能
## fadeOut:淡出
```
view.run {
    launch(this.fadeOut())
}
```

## fadeIn:淡入
```
view.run {
    launch(this.fadeIn())
}
```

## shake:抖动
```
view.run {
    launch(this.shake())
}
```

## press:按压
```
view.run {
    launch(this.press())
}
```

## text:文字变换
```
testview.run {
    launch(this.text("Pateo", ANIMATION_DURATION, reverse = true))
}
```


