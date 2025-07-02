/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.chan.texas.misc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.PrintWriter;


public class RectF {
    public float left;
    public float top;
    public float right;
    public float bottom;
    
    
    public RectF() {}

    
    public RectF(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    
    public RectF(@Nullable RectF r) {
        if (r == null) {
            left = top = right = bottom = 0.0f;
        } else {
            left = r.left;
            top = r.top;
            right = r.right;
            bottom = r.bottom;
        }
    }
    
    public RectF(@Nullable Rect r) {
        if (r == null) {
            left = top = right = bottom = 0.0f;
        } else {
            left = r.left;
            top = r.top;
            right = r.right;
            bottom = r.bottom;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RectF r = (RectF) o;
        return left == r.left && top == r.top && right == r.right && bottom == r.bottom;
    }

    @Override
    public int hashCode() {
        int result = (left != +0.0f ? Float.floatToIntBits(left) : 0);
        result = 31 * result + (top != +0.0f ? Float.floatToIntBits(top) : 0);
        result = 31 * result + (right != +0.0f ? Float.floatToIntBits(right) : 0);
        result = 31 * result + (bottom != +0.0f ? Float.floatToIntBits(bottom) : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RectF(" + left + ", " + top + ", "
                      + right + ", " + bottom + ")";
    }

    
    @NonNull
    public String toShortString() {
        return toShortString(new StringBuilder(32));
    }
    
    
    @NonNull
    public String toShortString(@NonNull StringBuilder sb) {
        sb.setLength(0);
        sb.append('['); sb.append(left); sb.append(',');
        sb.append(top); sb.append("]["); sb.append(right);
        sb.append(','); sb.append(bottom); sb.append(']');
        return sb.toString();
    }
    
    
    public void printShortString(@NonNull PrintWriter pw) {
        pw.print('['); pw.print(left); pw.print(',');
        pw.print(top); pw.print("]["); pw.print(right);
        pw.print(','); pw.print(bottom); pw.print(']');
    }

    
    public final boolean isEmpty() {
        return left >= right || top >= bottom;
    }

    
    public final float width() {
        return right - left;
    }

    
    public final float height() {
        return bottom - top;
    }

    
    public final float centerX() {
        return (left + right) * 0.5f;
    }

    
    public final float centerY() {
        return (top + bottom) * 0.5f;
    }
    
    
    public void setEmpty() {
        left = right = top = bottom = 0;
    }
    
    
    public void set(float left, float top, float right, float bottom) {
        this.left   = left;
        this.top    = top;
        this.right  = right;
        this.bottom = bottom;
    }

    
    public void set(@NonNull RectF src) {
        this.left   = src.left;
        this.top    = src.top;
        this.right  = src.right;
        this.bottom = src.bottom;
    }
    
    
    public void set(@NonNull Rect src) {
        this.left   = src.left;
        this.top    = src.top;
        this.right  = src.right;
        this.bottom = src.bottom;
    }

    
    public void offset(float dx, float dy) {
        left    += dx;
        top     += dy;
        right   += dx;
        bottom  += dy;
    }

    
    public void offsetTo(float newLeft, float newTop) {
        right += newLeft - left;
        bottom += newTop - top;
        left = newLeft;
        top = newTop;
    }
    
    
    public void inset(float dx, float dy) {
        left    += dx;
        top     += dy;
        right   -= dx;
        bottom  -= dy;
    }

    
    public boolean contains(float x, float y) {
        return left < right && top < bottom  
                && x >= left && x < right && y >= top && y < bottom;
    }
    
    
    public boolean contains(float left, float top, float right, float bottom) {

        return this.left < this.right && this.top < this.bottom

                && this.left <= left && this.top <= top
                && this.right >= right && this.bottom >= bottom;
    }
    
    
    public boolean contains(@NonNull RectF r) {

        return this.left < this.right && this.top < this.bottom

                && left <= r.left && top <= r.top
                && right >= r.right && bottom >= r.bottom;
    }
    
    
    public boolean intersect(float left, float top, float right, float bottom) {
        if (this.left < right && left < this.right
                && this.top < bottom && top < this.bottom) {
            if (this.left < left) {
                this.left = left;
            }
            if (this.top < top) {
                this.top = top;
            }
            if (this.right > right) {
                this.right = right;
            }
            if (this.bottom > bottom) {
                this.bottom = bottom;
            }
            return true;
        }
        return false;
    }
    
    
    public boolean intersect(@NonNull RectF r) {
        return intersect(r.left, r.top, r.right, r.bottom);
    }
    
    
    public boolean setIntersect(@NonNull RectF a, @NonNull RectF b) {
        if (a.left < b.right && b.left < a.right
                && a.top < b.bottom && b.top < a.bottom) {
            left = Math.max(a.left, b.left);
            top = Math.max(a.top, b.top);
            right = Math.min(a.right, b.right);
            bottom = Math.min(a.bottom, b.bottom);
            return true;
        }
        return false;
    }
    
    
    public boolean intersects(float left, float top, float right,
                              float bottom) {
        return this.left < right && left < this.right
                && this.top < bottom && top < this.bottom;
    }
    
    
    public static boolean intersects(@NonNull RectF a, @NonNull RectF b) {
        return a.left < b.right && b.left < a.right
                && a.top < b.bottom && b.top < a.bottom;
    }

    
    public void roundOut(@NonNull Rect dst) {
        dst.set((int) Math.floor(left), (int) Math.floor(top),
                (int) Math.ceil(right), (int) Math.ceil(bottom));
    }

    
    public void union(float left, float top, float right, float bottom) {
        if ((left < right) && (top < bottom)) {
            if ((this.left < this.right) && (this.top < this.bottom)) {
                if (this.left > left)
                    this.left = left;
                if (this.top > top)
                    this.top = top;
                if (this.right < right)
                    this.right = right;
                if (this.bottom < bottom)
                    this.bottom = bottom;
            } else {
                this.left = left;
                this.top = top;
                this.right = right;
                this.bottom = bottom;
            }
        }
    }
    
    
    public void union(@NonNull RectF r) {
        union(r.left, r.top, r.right, r.bottom);
    }
    
    
    public void union(float x, float y) {
        if (x < left) {
            left = x;
        } else if (x > right) {
            right = x;
        }
        if (y < top) {
            top = y;
        } else if (y > bottom) {
            bottom = y;
        }
    }
    
    
    public void sort() {
        if (left > right) {
            float temp = left;
            left = right;
            right = temp;
        }
        if (top > bottom) {
            float temp = top;
            top = bottom;
            bottom = temp;
        }
    }

    
    public void scale(float scale) {
        if (scale != 1.0f) {
            left = left * scale;
            top = top * scale ;
            right = right * scale;
            bottom = bottom * scale;
        }
    }
}
