package me.chan.texas.ext.markdown.math.renderer;

import me.chan.texas.ext.markdown.math.renderer.core.MathCanvas;
import me.chan.texas.ext.markdown.math.renderer.core.MathPaint;

/**
 * PhantomNode 用于实现 LaTeX 的 phantom 命令
 * - \phantom: 占用内容的宽度和高度，但不显示内容
 * - \hphantom: 仅占用内容的宽度，高度为0
 * - \vphantom: 仅占用内容的高度，宽度为0
 */
public class PhantomNode extends RendererNode {
    
    private final RendererNode mContent;
    private final boolean mUseWidth;
    private final boolean mUseHeight;
    
    /**
     * 创建一个 PhantomNode
     * 
     * @param styles 样式
     * @param content 内容节点（用于计算尺寸）
     * @param useWidth 是否使用内容的宽度
     * @param useHeight 是否使用内容的高度
     */
    public PhantomNode(MathPaint.Styles styles, RendererNode content, boolean useWidth, boolean useHeight) {
        super(styles);
        mContent = content;
        mUseWidth = useWidth;
        mUseHeight = useHeight;
    }
    
    @Override
    protected void onMeasure(MathPaint paint, int widthSpec, int heightSpec) {
        // 测量内容节点以获取其尺寸
        mContent.measure(paint, widthSpec, heightSpec);
        
        // 根据参数决定使用哪些尺寸
        int width = mUseWidth ? mContent.getWidth() : 0;
        int height = mUseHeight ? mContent.getHeight() : 0;
        
        setMeasuredSize(width, height);
    }
    
    @Override
    protected void onLayoutChildren() {
        // 布局子节点（虽然不会绘制，但需要正确的布局用于基线计算）
        mContent.layout(0, 0);
    }
    
    @Override
    public float getBaseline() {
        // 基线位置根据是否使用高度来决定
        if (mUseHeight) {
            // 如果使用高度，返回内容的基线
            return mContent.getBaseline();
        } else {
            // 如果不使用高度，返回当前位置（通常是 bottom）
            return getBottom();
        }
    }
    
    @Override
    protected void onDraw(MathCanvas canvas, MathPaint paint) {
        // Phantom 节点不绘制任何内容
        // 这是 phantom 命令的核心：占用空间但不显示
        /* NOOP - 不绘制任何内容 */
    }
    
    @Override
    protected void onDrawDebug(MathCanvas canvas, MathPaint paint) {
        // 在调试模式下，可以选择显示或不显示边界
        // 这里选择不显示，避免调试输出干扰
        /* NOOP - 在调试模式下也不显示 */
    }
    
    @Override
    protected String toPretty() {
        StringBuilder sb = new StringBuilder("phantom[");
        if (mUseWidth && mUseHeight) {
            sb.append("w+h");
        } else if (mUseWidth) {
            sb.append("w");
        } else if (mUseHeight) {
            sb.append("h");
        } else {
            sb.append("none");
        }
        sb.append("]: ");
        sb.append(mContent.toPretty());
        return sb.toString();
    }
}