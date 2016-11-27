package Aopproxy;

import java.lang.reflect.Method;

public class PrintBeforeAdvice implements MethodBeforeAdvice{

	public PrintBeforeAdvice() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void before(Method method, Object[] args, Object target) throws Throwable {
		System.out.println("Call PrintBeforeAdvice Success!");
	}
}
