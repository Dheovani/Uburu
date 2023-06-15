package br.com.uburu.spring;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import br.com.uburu.spring.utils.Observer;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		var context = new SpringApplicationBuilder(Application.class).headless(false).run(args);

		// Observar os repositório em busca de mudanças
		Observer observer = context.getBean(Observer.class);
		observer.watch();
	}

}
