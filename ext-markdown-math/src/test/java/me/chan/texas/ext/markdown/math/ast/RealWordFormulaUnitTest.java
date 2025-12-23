package me.chan.texas.ext.markdown.math.ast;

import static me.chan.texas.ext.markdown.unitTest.MathParserUnitTest.assertParsesTo;

import org.junit.Test;

public class RealWordFormulaUnitTest {


	// ============================================================
	// Part 9: 真实世界公式（继续补充）
	// ============================================================

	@Test
	public void test_09_26_Real_CauchyIntegralFormula() {
		System.out.println("\n=== Part 9.26: 柯西积分公式 ===");

		assertParsesTo(
				"f\\left(z_0\\right)=\\frac{1}{2\\pi i}\\oint_C\\frac{f\\left(z\\right)}{z-z_0}dz",
				"f\\left( z_0 \\right)=\\frac{1}{2\\pii}\\oint_C\\frac{f\\left( z \\right)}{z-z_0}dz"
		);
	}

	@Test
	public void test_09_27_Real_BinomialTheorem() {
		System.out.println("\n=== Part 9.27: 二项式定理 ===");

		assertParsesTo(
				"\\left(x+y\\right)^n=\\sum_{k=0}^{n}\\binom{n}{k}x^{n-k}y^k",
				"\\left( x+y \\right)^n=\\sum_{k=0}^{n}\\binom{n}{k}x^{n-k}y^k"
		);
	}

	@Test
	public void test_09_28_Real_CentralLimitTheorem() {
		System.out.println("\n=== Part 9.28: 中心极限定理 ===");

		assertParsesTo(
				"\\frac{\\bar{X}_n-\\mu}{\\sigma/\\sqrt{n}}\\xrightarrow{d}N\\left(0,1\\right)",
				"\\frac{\\bar{X}_n-\\mu}{\\sigma/\\sqrt{n}}\\xrightarrow{d}N\\left( 0,1 \\right)"
		);
	}

	@Test
	public void test_09_29_Real_NormalDistribution() {
		System.out.println("\n=== Part 9.29: 正态分布 ===");

		assertParsesTo(
				"f\\left(x\\right)=\\frac{1}{\\sigma\\sqrt{2\\pi}}e^{-\\frac{\\left(x-\\mu\\right)^2}{2\\sigma^2}}",
				"f\\left( x \\right)=\\frac{1}{\\sigma\\sqrt{2\\pi}}e^{-\\frac{\\left( x-\\mu \\right)^2}{2\\sigma^2}}"
		);
	}

	@Test
	public void test_09_30_Real_HeatEquation() {
		System.out.println("\n=== Part 9.30: 热传导方程 ===");

		assertParsesTo(
				"\\frac{\\partial u}{\\partial t}=\\alpha\\nabla^2u",
				"\\frac{\\partialu}{\\partialt}=\\alpha\\nabla^2u"
		);
	}

	@Test
	public void test_09_31_Real_LaplaceEquation() {
		System.out.println("\n=== Part 9.31: 拉普拉斯方程 ===");

		assertParsesTo(
				"\\nabla^2\\phi=\\frac{\\partial^2\\phi}{\\partial x^2}+\\frac{\\partial^2\\phi}{\\partial y^2}+\\frac{\\partial^2\\phi}{\\partial z^2}=0",
				"\\nabla^2\\phi=\\frac{\\partial^2\\phi}{\\partialx^2}+\\frac{\\partial^2\\phi}{\\partialy^2}+\\frac{\\partial^2\\phi}{\\partialz^2}=0"
		);
	}

	@Test
	public void test_09_32_Real_PoissonEquation() {
		System.out.println("\n=== Part 9.32: 泊松方程 ===");

		assertParsesTo(
				"\\nabla^2\\phi=-\\frac{\\rho}{\\epsilon_0}",
				"\\nabla^2\\phi=-\\frac{\\rho}{\\epsilon_0}"
		);
	}

	@Test
	public void test_09_33_Real_LeibnizIntegralRule() {
		System.out.println("\n=== Part 9.33: 莱布尼茨积分法则 ===");

		assertParsesTo(
				"\\frac{d}{dx}\\int_{a\\left(x\\right)}^{b\\left(x\\right)}f\\left(x,t\\right)dt=\\int_{a\\left(x\\right)}^{b\\left(x\\right)}\\frac{\\partial f}{\\partial x}dt+f\\left(x,b\\left(x\\right)\\right)\\frac{db}{dx}-f\\left(x,a\\left(x\\right)\\right)\\frac{da}{dx}",
				"\\frac{d}{dx}\\int_{a\\left( x \\right)}^{b\\left( x\\right)}f\\left( x,t \\right)dt=\\int_{a\\left( x \\right)}^{b\\left( x\\right)}\\frac{\\partialf}{\\partialx}dt+f\\left( x,b\\left( x \\right)\\right)\\frac{db}{dx}-f\\left( x,a \\left( x \\right)\\right)\\frac{da}{dx}"
		);
	}

	@Test
	public void test_09_34_Real_EulerLagrangeEquation() {
		System.out.println("\n=== Part 9.34: 欧拉-拉格朗日方程（泛函形式）===");

		assertParsesTo(
				"\\frac{\\partial L}{\\partial y}-\\frac{d}{dx}\\frac{\\partial L}{\\partial y'}=0",
				"\\frac{\\partial L}{\\partial y}-\\frac{d}{dx} \\frac{\\partial L}{\\partial y'}=0"
		);
	}

	@Test
	public void test_09_35_Real_HamiltonEquations() {
		System.out.println("\n=== Part 9.35: 哈密顿正则方程 ===");

		// 正则方程组
		assertParsesTo(
				"\\dot{q}_i=\\frac{\\partial H}{\\partial p_i},\\quad\\dot{p}_i=-\\frac{\\partial H}{\\partial q_i}",
				"\\dot{q}_i=\\frac{\\partial H}{\\partial p_i},\\quad \\dot{p}_i=-\\frac{\\partial H}{\\partial q_i}"
		);
	}

	@Test
	public void test_09_36_Real_NoetherTheorem() {
		System.out.println("\n=== Part 9.36: 诺特定理 ===");

		assertParsesTo(
				"Q=\\sum_i\\frac{\\partial L}{\\partial\\dot{q}_i}\\frac{\\partial q_i}{\\partial s}",
				"Q=\\sum_i \\frac{\\partial L}{\\partial \\dot{q}_i} \\frac{\\partial q_i}{\\partial s}"
		);
	}

	@Test
	public void test_09_37_Real_HeisenbergUncertaintyPrinciple() {
		System.out.println("\n=== Part 9.37: 海森堡不确定性原理 ===");

		assertParsesTo(
				"\\Delta x\\cdot\\Delta p\\ge\\frac{\\hbar}{2}",
				"\\Delta x \\cdot \\Delta p \\ge \\frac{\\hbar}{2}"
		);
	}

	@Test
	public void test_09_38_Real_BoltzmannEntropy() {
		System.out.println("\n=== Part 9.38: 玻尔兹曼熵公式 ===");

		assertParsesTo(
				"S=k_B\\ln\\Omega",
				"S=k_B \\ln \\Omega"
		);
	}

	@Test
	public void test_09_39_Real_PlanckRadiationLaw() {
		System.out.println("\n=== Part 9.39: 普朗克辐射定律 ===");

		assertParsesTo(
				"B_\\nu\\left(T\\right)=\\frac{2h\\nu^3}{c^2}\\frac{1}{e^{\\frac{h\\nu}{k_BT}}-1}",
				"B_\\nu \\left( T \\right)=\\frac{2 h \\nu^3}{c^2} \\frac{1}{e^{\\frac{h \\nu}{k_B T}}-1}"
		);
	}

	@Test
	public void test_09_40_Real_LaplaceTransform() {
		System.out.println("\n=== Part 9.40: 拉普拉斯变换 ===");

		assertParsesTo(
				"F\\left(s\\right)=\\mathcal{L}\\left\\{f\\left(t\\right)\\right\\}=\\int_0^{\\infty}f\\left(t\\right)e^{-st}dt",
				"F \\left( s \\right)=\\mathcal{L} \\left{ f \\left( t \\right) \\right}=\\int_0^{\\infty} f \\left( t \\right) e^{-st} dt"
		);
	}

	@Test
	public void test_09_41_Real_ConvolutionTheorem() {
		System.out.println("\n=== Part 9.41: 卷积定理 ===");

		assertParsesTo(
				"\\left(f*g\\right)\\left(t\\right)=\\int_{-\\infty}^{\\infty}f\\left(\\tau\\right)g\\left(t-\\tau\\right)d\\tau",
				"\\left( f * g \\right) \\left( t \\right)=\\int_{-\\infty}^{\\infty} f \\left( \\tau \\right) g \\left( t-\\tau \\right) d \\tau"
		);
	}

	@Test
	public void test_09_42_Real_StirlingApproximation() {
		System.out.println("\n=== Part 9.42: 斯特林近似 ===");

		assertParsesTo(
				"n!\\approx\\sqrt{2\\pi n}\\left(\\frac{n}{e}\\right)^n",
				"n! \\approx \\sqrt{2 \\pi n} \\left( \\frac{n}{e} \\right)^n"
		);
	}

	@Test
	public void test_09_43_Real_EulerProduct() {
		System.out.println("\n=== Part 9.43: 欧拉乘积公式 ===");

		assertParsesTo(
				"\\zeta\\left(s\\right)=\\prod_{p\\text{ prime}}\\left(1-p^{-s}\\right)^{-1}",
				"\\zeta \\left( s \\right)=\\prod_{p \\text{ prime}} \\left( 1-p^{-s} \\right)^{-1}"
		);
	}

	@Test
	public void test_09_44_Real_RiemannHypothesis() {
		System.out.println("\n=== Part 9.44: 黎曼猜想（非平凡零点）===");

		assertParsesTo(
				"\\zeta\\left(s\\right)=0\\implies\\Re\\left(s\\right)=\\frac{1}{2}",
				"\\zeta \\left( s \\right)=0 \\implies \\Re \\left( s \\right)=\\frac{1}{2}"
		);
	}

	@Test
	public void test_09_45_Real_VandermondeIdentity() {
		System.out.println("\n=== Part 9.45: 范德蒙德恒等式 ===");

		assertParsesTo(
				"\\binom{m+n}{r}=\\sum_{k=0}^{r}\\binom{m}{k}\\binom{n}{r-k}",
				"\\binom{m+n}{r}=\\sum_{k=0}^{r} \\binom{m}{k} \\binom{n}{r-k}"
		);
	}

	@Test
	public void test_09_46_Real_CauchyRiemannEquations() {
		System.out.println("\n=== Part 9.46: 柯西-黎曼方程 ===");

		assertParsesTo(
				"\\frac{\\partial u}{\\partial x}=\\frac{\\partial v}{\\partial y},\\quad\\frac{\\partial u}{\\partial y}=-\\frac{\\partial v}{\\partial x}",
				"\\frac{\\partial u}{\\partial x}=\\frac{\\partial v}{\\partial y},\\quad \\frac{\\partial u}{\\partial y}=-\\frac{\\partial v}{\\partial x}"
		);
	}

	@Test
	public void test_09_47_Real_RodriguesFormula() {
		System.out.println("\n=== Part 9.47: 罗德里格斯公式（勒让德多项式）===");

		assertParsesTo(
				"P_n\\left(x\\right)=\\frac{1}{2^n n!}\\frac{d^n}{dx^n}\\left(x^2-1\\right)^n",
				"P_n \\left( x \\right)=\\frac{1}{2^n n!} \\frac{d^n}{dx^n} \\left( x^2-1 \\right)^n"
		);
	}

	@Test
	public void test_09_48_Real_LegendrePolynomialOrthogonality() {
		System.out.println("\n=== Part 9.48: 勒让德多项式正交性 ===");

		assertParsesTo(
				"\\int_{-1}^{1}P_m\\left(x\\right)P_n\\left(x\\right)dx=\\frac{2}{2n+1}\\delta_{mn}",
				"\\int_{-1}^{1} P_m \\left( x \\right) P_n \\left( x \\right) dx=\\frac{2}{2 n+1} \\delta_{mn}"
		);
	}

	@Test
	public void test_09_49_Real_BesselEquation() {
		System.out.println("\n=== Part 9.49: 贝塞尔方程 ===");

		assertParsesTo(
				"x^2\\frac{d^2y}{dx^2}+x\\frac{dy}{dx}+\\left(x^2-\\nu^2\\right)y=0",
				"x^2 \\frac{d^2 y}{dx^2}+x \\frac{dy}{dx}+\\left( x^2-\\nu^2 \\right) y=0"
		);
	}

	@Test
	public void test_09_50_Real_GramSchmidtProcess() {
		System.out.println("\n=== Part 9.50: 格拉姆-施密特正交化 ===");

		assertParsesTo(
				"\\mathbf{u}_k=\\mathbf{v}_k-\\sum_{j=1}^{k-1}\\text{proj}_{\\mathbf{u}_j}\\left(\\mathbf{v}_k\\right)",
				"\\mathbf{u}_k=\\mathbf{v}_k-\\sum_{j=1}^{k-1} \\text{proj}_{\\mathbf{u}_j} \\left( \\mathbf{v}_k \\right)"
		);
	}

	@Test
	public void test_09_51_Real_DivergenceTheorem() {
		System.out.println("\n=== Part 9.51: 散度定理（高斯定理）===");

		assertParsesTo(
				"\\iiint_V\\left(\\nabla\\cdot\\mathbf{F}\\right)dV=\\iint_{\\partial V}\\mathbf{F}\\cdot d\\mathbf{S}",
				"\\iiint_V \\left( \\nabla \\cdot \\mathbf{F} \\right) dV=\\iint_{\\partial V} \\mathbf{F} \\cdot d \\mathbf{S}"
		);
	}

	@Test
	public void test_09_52_Real_LorentzTransformation() {
		System.out.println("\n=== Part 9.52: 洛伦兹变换 ===");

		assertParsesTo(
				"x'=\\gamma\\left(x-vt\\right),\\quad t'=\\gamma\\left(t-\\frac{vx}{c^2}\\right),\\quad\\gamma=\\frac{1}{\\sqrt{1-\\frac{v^2}{c^2}}}",
				"x'=\\gamma\\left( x-vt \\right),\\quad t'=\\gamma\\left( t-\\frac{vx}{c^2} \\right),\\quad \\gamma=\\frac{1}{\\sqrt{1-\\frac{v^2}{c^2}}}"
		);
	}

	@Test
	public void test_09_53_Real_MinkowskiMetric() {
		System.out.println("\n=== Part 9.53: 闵可夫斯基度规 ===");

		assertParsesTo(
				"ds^2=-c^2dt^2+dx^2+dy^2+dz^2",
				"ds^2=-c^2 dt^2+dx^2+dy^2+dz^2"
		);
	}

	@Test
	public void test_09_54_Real_KleinGordonEquation() {
		System.out.println("\n=== Part 9.54: 克莱因-戈登方程 ===");

		assertParsesTo(
				"\\left(\\frac{1}{c^2}\\frac{\\partial^2}{\\partial t^2}-\\nabla^2+\\frac{m^2c^2}{\\hbar^2}\\right)\\psi=0",
				"\\left( \\frac{1}{c^2} \\frac{\\partial^2}{\\partial t^2}-\\nabla^2+\\frac{m^2 c^2}{\\hbar^2} \\right) \\psi=0"
		);
	}

	@Test
	public void test_09_55_Real_FeynmanPathIntegral() {
		System.out.println("\n=== Part 9.55: 费曼路径积分 ===");

		assertParsesTo(
				"K\\left(x_b,t_b;x_a,t_a\\right)=\\int\\mathcal{D}\\left[x\\left(t\\right)\\right]e^{\\frac{i}{\\hbar}S\\left[x\\left(t\\right)\\right]}",
				"K \\left( x_b, t_b ; x_a, t_a \\right)=\\int \\mathcal{D} \\left[ x \\left( t \\right) \\right] e^{\\frac{i}{\\hbar} S \\left[ x \\left( t \\right) \\right]}"
		);
	}

	@Test
	public void test_09_56_Real_ChernSimonsAction() {
		System.out.println("\n=== Part 9.56: 陈-西蒙斯作用量 ===");

		assertParsesTo(
				"S_{CS}=\\frac{k}{4\\pi}\\int\\text{Tr}\\left(A\\wedge dA+\\frac{2}{3}A\\wedge A\\wedge A\\right)",
				"S_{CS}=\\frac{k}{4 \\pi} \\int \\text{Tr} \\left( A \\wedge dA+\\frac{2}{3} A \\wedge A \\wedge A \\right)"
		);
	}

	@Test
	public void test_09_57_Real_YangMillsEquations() {
		System.out.println("\n=== Part 9.57: 杨-米尔斯方程 ===");

		assertParsesTo(
				"D_\\mu F^{\\mu\\nu}=j^\\nu",
				"D_\\mu F^{\\mu \\nu}=j^\\nu"
		);
	}

	@Test
	public void test_09_58_Real_HodgeDecomposition() {
		System.out.println("\n=== Part 9.58: 霍奇分解 ===");

		assertParsesTo(
				"\\omega=d\\alpha+\\delta\\beta+\\gamma",
				"\\omega=d \\alpha+\\delta \\beta+\\gamma"
		);
	}

	@Test
	public void test_09_59_Real_AtiyahSingerIndex() {
		System.out.println("\n=== Part 9.59: 阿蒂亚-辛格指标定理 ===");

		assertParsesTo(
				"\\text{ind}\\left(D\\right)=\\int_M\\hat{A}\\left(M\\right)\\text{ch}\\left(E\\right)",
				"\\text{ind} \\left( D \\right)=\\int_M \\hat{A} \\left( M \\right) \\text{ch} \\left( E \\right)"
		);
	}

	@Test
	public void test_09_60_Real_PartitionFunction() {
		System.out.println("\n=== Part 9.60: 配分函数（统计力学）===");

		assertParsesTo(
				"Z=\\sum_i e^{-\\beta E_i}=\\text{Tr}\\left(e^{-\\beta H}\\right)",
				"Z=\\sum_i e^{-\\beta E_i}=\\text{Tr} \\left( e^{-\\beta H} \\right)"
		);
	}


	// ============================================================
	// Part 9: 真实世界公式
	// ============================================================

	@Test
	public void test_09_11_Real_SchrodingerEquation() {
		System.out.println("\n=== Part 9.11: 薛定谔方程 ===");

		assertParsesTo(
				"i\\hbar\\frac{\\partial}{\\partial t}\\Psi\\left(r,t\\right)=\\left[-\\frac{\\hbar^2}{2m}\\nabla^2+V\\left(r,t\\right)\\right]\\Psi\\left(r,t\\right)",
				"i \\hbar \\frac{\\partial}{\\partial t} \\Psi \\left( r, t \\right)=\\left[ -\\frac{\\hbar^2}{2 m} \\nabla^2+V \\left( r, t \\right) \\right] \\Psi \\left( r, t \\right)"
		);
	}

	@Test
	public void test_09_12_Real_MaxwellEquations() {
		System.out.println("\n=== Part 9.12: 麦克斯韦方程组 ===");

		// 高斯定律
		assertParsesTo(
				"\\nabla\\cdot\\mathbf{E}=\\frac{\\rho}{\\epsilon_0}",
				"\\nabla \\cdot \\mathbf{E}=\\frac{\\rho}{\\epsilon_0}"
		);

		// 高斯磁定律
		assertParsesTo(
				"\\nabla\\cdot\\mathbf{B}=0",
				"\\nabla \\cdot \\mathbf{B}=0"
		);

		// 法拉第电磁感应定律
		assertParsesTo(
				"\\nabla\\times\\mathbf{E}=-\\frac{\\partial\\mathbf{B}}{\\partial t}",
				"\\nabla \\times \\mathbf{E}=-\\frac{\\partial \\mathbf{B}}{\\partial t}"
		);

		// 安培-麦克斯韦定律
		assertParsesTo(
				"\\nabla\\times\\mathbf{B}=\\mu_0\\left(\\mathbf{J}+\\epsilon_0\\frac{\\partial\\mathbf{E}}{\\partial t}\\right)",
				"\\nabla \\times \\mathbf{B}=\\mu_0 \\left( \\mathbf{J}+\\epsilon_0 \\frac{\\partial \\mathbf{E}}{\\partial t} \\right)"
		);
	}

	@Test
	public void test_09_13_Real_EinsteinFieldEquation() {
		System.out.println("\n=== Part 9.13: 爱因斯坦场方程 ===");

		assertParsesTo(
				"R_{\\mu\\nu}-\\frac{1}{2}Rg_{\\mu\\nu}+\\Lambda g_{\\mu\\nu}=\\frac{8\\pi G}{c^4}T_{\\mu\\nu}",
				"R_{\\mu \\nu}-\\frac{1}{2} Rg_{\\mu \\nu}+\\Lambda g_{\\mu \\nu}=\\frac{8 \\pi G}{c^4} T_{\\mu \\nu}"
		);
	}

	@Test
	public void test_09_14_Real_FourierTransform() {
		System.out.println("\n=== Part 9.14: 傅里叶变换 ===");

		assertParsesTo(
				"F\\left(\\omega\\right)=\\int_{-\\infty}^{\\infty}f\\left(t\\right)e^{-i\\omega t}dt",
				"F \\left( \\omega \\right)=\\int_{-\\infty}^{\\infty} f \\left( t \\right) e^{-i \\omega t} dt"
		);

		// 逆傅里叶变换
		assertParsesTo(
				"f\\left(t\\right)=\\frac{1}{2\\pi}\\int_{-\\infty}^{\\infty}F\\left(\\omega\\right)e^{i\\omega t}d\\omega",
				"f \\left( t \\right)=\\frac{1}{2 \\pi} \\int_{-\\infty}^{\\infty} F \\left( \\omega \\right) e^{i \\omega t} d \\omega"
		);
	}

	@Test
	public void test_09_15_Real_RiemannZetaFunction() {
		System.out.println("\n=== Part 9.15: 黎曼ζ函数 ===");

		assertParsesTo(
				"\\zeta\\left(s\\right)=\\sum_{n=1}^{\\infty}\\frac{1}{n^s}=\\prod_{p\\text{ prime}}\\frac{1}{1-p^{-s}}",
				"\\zeta \\left( s \\right)=\\sum_{n=1}^{\\infty} \\frac{1}{n^s}=\\prod_{p \\text{ prime}} \\frac{1}{1-p^{-s}}"
		);
	}

	@Test
	public void test_09_16_Real_BayesTheorem() {
		System.out.println("\n=== Part 9.16: 贝叶斯定理（连续形式）===");

		assertParsesTo(
				"P\\left(\\theta|D\\right)=\\frac{P\\left(D|\\theta\\right)P\\left(\\theta\\right)}{\\int P\\left(D|\\theta'\\right)P\\left(\\theta'\\right)d\\theta'}",
				"P \\left( \\theta | D \\right)=\\frac{P \\left( D | \\theta \\right) P \\left( \\theta \\right)}{\\int P \\left( D | \\theta' \\right) P \\left( \\theta' \\right) d \\theta'}"
		);
	}

	@Test
	public void test_09_17_Real_LagrangeEquation() {
		System.out.println("\n=== Part 9.17: 拉格朗日方程 ===");

		assertParsesTo(
				"\\frac{d}{dt}\\left(\\frac{\\partial L}{\\partial\\dot{q}_i}\\right)-\\frac{\\partial L}{\\partial q_i}=0",
				"\\frac{d}{dt} \\left( \\frac{\\partial L}{\\partial \\dot{q}_i} \\right)-\\frac{\\partial L}{\\partial q_i}=0"
		);
	}

	@Test
	public void test_09_18_Real_NavierStokesEquation() {
		System.out.println("\n=== Part 9.18: 纳维-斯托克斯方程 ===");

		assertParsesTo(
				"\\rho\\left(\\frac{\\partial\\mathbf{v}}{\\partial t}+\\mathbf{v}\\cdot\\nabla\\mathbf{v}\\right)=-\\nabla p+\\mu\\nabla^2\\mathbf{v}+\\mathbf{f}",
				"\\rho \\left( \\frac{\\partial \\mathbf{v}}{\\partial t}+\\mathbf{v} \\cdot \\nabla \\mathbf{v} \\right)=-\\nabla p+\\mu \\nabla^2 \\mathbf{v}+\\mathbf{f}"
		);
	}

	@Test
	public void test_09_19_Real_BlackScholesEquation() {
		System.out.println("\n=== Part 9.19: 布莱克-舒尔斯方程 ===");

		assertParsesTo(
				"\\frac{\\partial V}{\\partial t}+\\frac{1}{2}\\sigma^2S^2\\frac{\\partial^2V}{\\partial S^2}+rS\\frac{\\partial V}{\\partial S}-rV=0",
				"\\frac{\\partial V}{\\partial t}+\\frac{1}{2} \\sigma^2 S^2 \\frac{\\partial^2 V}{\\partial S^2}+rS \\frac{\\partial V}{\\partial S}-rV=0"
		);
	}

	@Test
	public void test_09_20_Real_DiracEquation() {
		System.out.println("\n=== Part 9.20: 狄拉克方程 ===");

		assertParsesTo(
				"\\left(i\\gamma^\\mu\\partial_\\mu-m\\right)\\psi=0",
				"\\left( i \\gamma^\\mu \\partial_\\mu-m \\right) \\psi=0"
		);
	}

	@Test
	public void test_09_21_Real_GreensTheorem() {
		System.out.println("\n=== Part 9.21: 格林定理 ===");

		assertParsesTo(
				"\\oint_C\\left(P dx+Q dy\\right)=\\iint_D\\left(\\frac{\\partial Q}{\\partial x}-\\frac{\\partial P}{\\partial y}\\right)dA",
				"\\oint_C \\left( P dx+Q dy \\right)=\\iint_D \\left( \\frac{\\partial Q}{\\partial x}-\\frac{\\partial P}{\\partial y} \\right) dA"
		);
	}

	@Test
	public void test_09_23_Real_StokesTheorem() {
		System.out.println("\n=== Part 9.23: 斯托克斯定理 ===");

		assertParsesTo(
				"\\int_S\\left(\\nabla\\times\\mathbf{F}\\right)\\cdot d\\mathbf{S}=\\oint_{\\partial S}\\mathbf{F}\\cdot d\\mathbf{r}",
				"\\int_S \\left( \\nabla \\times \\mathbf{F} \\right) \\cdot d \\mathbf{S}=\\oint_{\\partial S} \\mathbf{F} \\cdot d \\mathbf{r}"
		);
	}

	@Test
	public void test_09_24_Real_ComplexIntegralWithResidue() {
		System.out.println("\n=== Part 9.24: 留数定理 ===");

		assertParsesTo(
				"\\oint_C f\\left(z\\right)dz=2\\pi i\\sum_{k=1}^{n}\\text{Res}\\left(f,z_k\\right)",
				"\\oint_C f \\left( z \\right) dz=2 \\pi i \\sum_{k=1}^{n} \\text{Res} \\left( f, z_k \\right)"
		);
	}

	@Test
	public void test_09_25_Real_WaveEquation() {
		System.out.println("\n=== Part 9.25: 波动方程 ===");

		assertParsesTo(
				"\\frac{\\partial^2u}{\\partial t^2}=c^2\\nabla^2u",
				"\\frac{\\partial^2 u}{\\partial t^2}=c^2 \\nabla^2 u"
		);
	}

	@Test
	public void test_12_06_Binom_RealWorld() {
		System.out.println("\n=== Part 12.6: 二项式系数-真实用例 ===");

		// 二项式定理
		assertParsesTo(
				"\\left(x+y\\right)^n=\\sum_{k=0}^{n}\\binom{n}{k}x^{n-k}y^k",
				"\\left( x+y \\right)^n=\\sum_{k=0}^{n} \\binom{n}{k} x^{n-k} y^k"
		);

		// 范德蒙德恒等式
		assertParsesTo(
				"\\binom{m+n}{r}=\\sum_{k=0}^{r}\\binom{m}{k}\\binom{n}{r-k}",
				"\\binom{m+n}{r}=\\sum_{k=0}^{r} \\binom{m}{k} \\binom{n}{r-k}"
		);

		// 帕斯卡恒等式
		assertParsesTo(
				"\\binom{n}{k}=\\binom{n-1}{k-1}+\\binom{n-1}{k}",
				"\\binom{n}{k}=\\binom{n-1}{k-1}+\\binom{n-1}{k}"
		);

		// 组合数性质
		assertParsesTo(
				"\\binom{n}{k}=\\binom{n}{n-k}",
				"\\binom{n}{k}=\\binom{n}{n-k}"
		);

		// 二项式系数和
		assertParsesTo(
				"\\sum_{k=0}^{n}\\binom{n}{k}=2^n",
				"\\sum_{k=0}^{n} \\binom{n}{k}=2^n"
		);
	}

	@Test
	public void test_11_05_PostfixOp_Factorial_RealWorld() {
		System.out.println("\n=== Part 11.5: 后缀运算符-真实用例 ===");

		// 二项式系数定义
		assertParsesTo(
				"\\binom{n}{k}=\\frac{n!}{k!\\left(n-k\\right)!}",
				"\\binom{n}{k}=\\frac{n!}{k! \\left( n-k \\right)!}"
		);

		// 斯特林公式（已存在，验证阶乘部分）
		assertParsesTo(
				"n!\\approx\\sqrt{2\\pi n}\\left(\\frac{n}{e}\\right)^n",
				"n! \\approx \\sqrt{2 \\pi n} \\left( \\frac{n}{e} \\right)^n"
		);

		// 泰勒展开（验证阶乘）
		assertParsesTo(
				"e^x=\\sum_{n=0}^{\\infty}\\frac{x^n}{n!}",
				"e^x=\\sum_{n=0}^{\\infty} \\frac{x^n}{n!}"
		);
	}
}
