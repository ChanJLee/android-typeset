package me.chan.texas.renderer

import me.chan.texas.misc.RectF
import org.junit.Assert
import org.junit.Test

class RectDrawableUnitTest {

    @Test
    fun test() {
        var rect = RectF(0f, 1f, 100f, 99f)
        CompositeRectDrawable().apply {
            Assert.assertTrue(isEmpty)
            append(0f, 1f, 100f, 99f)
            Assert.assertFalse(isEmpty)
            Assert.assertEquals(rect, get(0))
            Assert.assertNotSame(rect, get(0))

            append(99.5f, 0f, 120f, 100f)
            Assert.assertFalse(isEmpty)
            Assert.assertEquals(1, size())
            var r = get(0)
            Assert.assertEquals(0f, r.left)
            Assert.assertEquals(0f, r.top)
            Assert.assertEquals(120f, r.right)
            Assert.assertEquals(100f, r.bottom)

            append(121f, 1f, 200f, 100f)
            Assert.assertFalse(isEmpty)
            Assert.assertEquals(2, size())
            r = get(0)
            Assert.assertEquals(0f, r.left)
            Assert.assertEquals(0f, r.top)
            Assert.assertEquals(120f, r.right)
            Assert.assertEquals(100f, r.bottom)

            r = get(1)
            Assert.assertEquals(121f, r.left)
            Assert.assertEquals(1f, r.top)
            Assert.assertEquals(200f, r.right)
            Assert.assertEquals(100f, r.bottom)
        }

        rect = RectF(100f, 1f, 200f, 99f)
        CompositeRectDrawable().apply {
            Assert.assertTrue(isEmpty)
            prepend(rect.left, rect.top, rect.right, rect.bottom)
            Assert.assertFalse(isEmpty)
            Assert.assertEquals(rect, get(0))
            Assert.assertNotSame(rect, get(0))

            prepend(50f, 0f, 99.5f, 100f)
            Assert.assertFalse(isEmpty)
            Assert.assertEquals(1, size())
            var r = get(0)
            Assert.assertEquals(50f, r.left)
            Assert.assertEquals(0f, r.top)
            Assert.assertEquals(200f, r.right)
            Assert.assertEquals(100f, r.bottom)

            prepend(0f, 0f, 45f, 100f)
            Assert.assertFalse(isEmpty)
            Assert.assertEquals(2, size())

            r = get(1)
            Assert.assertEquals(50f, r.left)
            Assert.assertEquals(0f, r.top)
            Assert.assertEquals(200f, r.right)
            Assert.assertEquals(100f, r.bottom)

            r = get(0)
            Assert.assertEquals(0f, r.left)
            Assert.assertEquals(0f, r.top)
            Assert.assertEquals(45f, r.right)
            Assert.assertEquals(100f, r.bottom)
        }

        val c1 = CompositeRectDrawable()
        val c2 = CompositeRectDrawable()

        val rectDrawable = RectDrawable()
        rectDrawable.append(c1)
        rectDrawable.prepend(c2);

        Assert.assertEquals(2, rectDrawable.size())
        Assert.assertSame(c2, rectDrawable.get(0))
        Assert.assertSame(c1, rectDrawable.get(1))
    }
}