package me.chan.texas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import me.chan.texas.debug.R;

public class MathDemoActivity extends AppCompatActivity {
	private MathView mMathView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_math_demo);

		RecyclerView recyclerView = findViewById(R.id.rv);
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
		recyclerView.setAdapter(new MathAdapter(getMathFormulas(), this::render));

		mMathView = findViewById(R.id.math);
	}

	private void render(String math) {
		mMathView.render(math);
	}

	private List<MathFormula> getMathFormulas() {
		List<MathFormula> formulas = new ArrayList<>();

		// 基础元素
		formulas.add(new MathFormula("整数", "123"));
		formulas.add(new MathFormula("小数", "3.14"));
		formulas.add(new MathFormula("变量", "x"));
		formulas.add(new MathFormula("带撇变量", "f'"));
		formulas.add(new MathFormula("希腊字母", "\\alpha"));

		// 运算符
		formulas.add(new MathFormula("一元负号", "-x"));
		formulas.add(new MathFormula("加减乘", "a + b - c \\times d"));
		formulas.add(new MathFormula("关系运算", "a \\neq b"));
		formulas.add(new MathFormula("集合运算", "A \\cup B"));
		formulas.add(new MathFormula("箭头", "a \\to b"));

		// 分式和根式
		formulas.add(new MathFormula("简单分式", "\\frac{1}{2}"));
		formulas.add(new MathFormula("复杂分式", "\\frac{x+y}{x-y}"));
		formulas.add(new MathFormula("平方根", "\\sqrt{x}"));
		formulas.add(new MathFormula("n次根", "\\sqrt[3]{27}"));

		// 定界符
		formulas.add(new MathFormula("圆括号", "\\left(x\\right)"));
		formulas.add(new MathFormula("方括号", "\\left[x\\right]"));
		formulas.add(new MathFormula("花括号", "\\left\\{x\\right\\}"));
		formulas.add(new MathFormula("绝对值", "\\left|x\\right|"));

		// 函数
		formulas.add(new MathFormula("三角函数", "\\sin x"));
		formulas.add(new MathFormula("对数函数", "\\log_2 x"));

		// 大型运算符
		formulas.add(new MathFormula("求和", "\\sum_{i=1}^{n}"));
		formulas.add(new MathFormula("积分", "\\int_0^1"));
		formulas.add(new MathFormula("极限", "\\lim_{x\\to 0}"));

		// 矩阵
		formulas.add(new MathFormula("矩阵", "\\begin{pmatrix}1&2\\\\3&4\\end{pmatrix}"));
		formulas.add(new MathFormula("分段函数", "\\begin{cases}x,&x\\ge 0\\\\-x,&x<0\\end{cases}"));

		// 上下标
		formulas.add(new MathFormula("上标", "x^2"));
		formulas.add(new MathFormula("下标", "x_i"));
		formulas.add(new MathFormula("上下标", "x^2_1"));

		// 重音和字体
		formulas.add(new MathFormula("hat重音", "\\hat{x}"));
		formulas.add(new MathFormula("向量", "\\vec{v}"));
		formulas.add(new MathFormula("粗体", "\\mathbf{x}"));
		formulas.add(new MathFormula("黑板粗体", "\\mathbb{R}"));

		// 二项式系数
		formulas.add(new MathFormula("二项式系数", "\\binom{n}{k}"));
		formulas.add(new MathFormula("阶乘", "n!"));

		// 真实公式
		formulas.add(new MathFormula("二次公式", "\\frac{-b\\pm\\sqrt{b^2-4ac}}{2a}"));
		formulas.add(new MathFormula("欧拉公式", "e^{i\\pi}+1=0"));
		formulas.add(new MathFormula("勾股定理", "a^2+b^2=c^2"));
		formulas.add(new MathFormula("极限定义", "\\lim_{x\\to 0}\\frac{\\sin x}{x}=1"));
		formulas.add(new MathFormula("求和公式", "\\sum_{n=1}^{\\infty}\\frac{1}{n^2}=\\frac{\\pi^2}{6}"));
		formulas.add(new MathFormula("高斯积分", "\\int_0^{\\infty}e^{-x^2}dx=\\frac{\\sqrt{\\pi}}{2}"));
		formulas.add(new MathFormula("柯西-施瓦茨不等式", "\\left(\\sum_{i=1}^{n}a_i b_i\\right)^2\\le\\left(\\sum_{i=1}^{n}a_i^2\\right)\\left(\\sum_{i=1}^{n}b_i^2\\right)"));
		formulas.add(new MathFormula("薛定谔方程", "i\\hbar\\frac{\\partial}{\\partial t}\\Psi\\left(r,t\\right)=\\left[-\\frac{\\hbar^2}{2m}\\nabla^2+V\\left(r,t\\right)\\right]\\Psi\\left(r,t\\right)"));
		formulas.add(new MathFormula("麦克斯韦方程(高斯定律)", "\\nabla\\cdot\\mathbf{E}=\\frac{\\rho}{\\epsilon_0}"));
		formulas.add(new MathFormula("傅里叶变换", "F\\left(\\omega\\right)=\\int_{-\\infty}^{\\infty}f\\left(t\\right)e^{-i\\omega t}dt"));
		formulas.add(new MathFormula("正态分布", "f\\left(x\\right)=\\frac{1}{\\sigma\\sqrt{2\\pi}}e^{-\\frac{\\left(x-\\mu\\right)^2}{2\\sigma^2}}"));
		formulas.add(new MathFormula("二项式定理", "\\left(x+y\\right)^n=\\sum_{k=0}^{n}\\binom{n}{k}x^{n-k}y^k"));
		formulas.add(new MathFormula("泰勒级数", "f\\left(x\\right)=\\sum_{n=0}^{\\infty}\\frac{f^{\\left(n\\right)}\\left(a\\right)}{n!}\\left(x-a\\right)^n"));
		formulas.add(new MathFormula("洛伦兹变换", "x'=\\gamma\\left(x-vt\\right),\\quad t'=\\gamma\\left(t-\\frac{vx}{c^2}\\right)"));
		formulas.add(new MathFormula("海森堡不确定性原理", "\\Delta x\\cdot\\Delta p\\ge\\frac{\\hbar}{2}"));
		formulas.add(new MathFormula("玻尔兹曼熵公式", "S=k_B\\ln\\Omega"));

		return formulas;
	}

	// 数据类
	private static class MathFormula {
		final String name;
		final String formula;

		MathFormula(String name, String formula) {
			this.name = name;
			this.formula = formula;
		}
	}

	// 点击回调接口
	interface OnFormulaClickListener {
		void onClick(String formula);
	}

	// Adapter
	private static class MathAdapter extends RecyclerView.Adapter<MathAdapter.ViewHolder> {
		private final List<MathFormula> mFormulas;
		private final OnFormulaClickListener mListener;

		MathAdapter(List<MathFormula> formulas, OnFormulaClickListener listener) {
			mFormulas = formulas;
			mListener = listener;
		}

		@NonNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.item_math_formula, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
			MathFormula formula = mFormulas.get(position);
			holder.tvName.setText(formula.name);
			holder.tvFormula.setText(formula.formula);
			holder.itemView.setOnClickListener(v -> mListener.onClick(formula.formula));
		}

		@Override
		public int getItemCount() {
			return mFormulas.size();
		}

		static class ViewHolder extends RecyclerView.ViewHolder {
			final TextView tvName;
			final TextView tvFormula;

			ViewHolder(View itemView) {
				super(itemView);
				tvName = itemView.findViewById(R.id.tv_name);
				tvFormula = itemView.findViewById(R.id.tv_formula);
			}
		}
	}
}