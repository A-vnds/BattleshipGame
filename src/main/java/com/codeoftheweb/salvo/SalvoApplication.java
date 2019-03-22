package com.codeoftheweb.salvo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class, args);
	}


	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository,
									  GameRepository gameRepository,
									  GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {

			Player player1 = new Player("j.bauer@ctu.gov", "24");
			playerRepository.save(player1);

			Player player2 = new Player( "c.obrian@ctu.gov","42");
			playerRepository.save(player2);

			Player player3 = new Player("kim_bauer@gmail.com","kb");
			playerRepository.save(player3);

			Player player4 = new Player("t.almeida@ctu.gov","mole");
			playerRepository.save(player4);

			Game game1 = new Game();
			gameRepository.save(game1);
			Date date1 = game1.getDate();

			Game game2 = new Game();
			game2.setDate(Date.from(date1.toInstant().plusSeconds(3600)));
			gameRepository.save(game2);

			Game game3 = new Game();
			game3.setDate(Date.from(date1.toInstant().plusSeconds(15000)));
			gameRepository.save(game3);

			Game game4 = new Game();
			game4.setDate(Date.from(date1.toInstant().plusSeconds(20000)));
			gameRepository.save(game4);


			Game game5 = new Game();
			game5.setDate(Date.from(date1.toInstant().plusSeconds(7200)));
			gameRepository.save(game5);




			GamePlayer gamePlayer1 = new GamePlayer(game1, player1);
			gamePlayerRepository.save(gamePlayer1);

			GamePlayer gamePlayer2 = new GamePlayer(game1, player2);
			gamePlayerRepository.save(gamePlayer2);

			GamePlayer gamePlayer3 = new GamePlayer(game2, player1);
			gamePlayerRepository.save(gamePlayer3);

			GamePlayer gamePlayer4 = new GamePlayer(game2, player2);
			gamePlayerRepository.save(gamePlayer4);

			GamePlayer gamePlayer5 = new GamePlayer(game3, player2);
			gamePlayerRepository.save(gamePlayer5);

			GamePlayer gamePlayer6 = new GamePlayer(game3, player4);
			gamePlayerRepository.save(gamePlayer6);

			GamePlayer gamePlayer7 = new GamePlayer(game4, player1);
			gamePlayerRepository.save(gamePlayer7);

//			GamePlayer gamePlayer8 = new GamePlayer(game4, player2);
//			gamePlayerRepository.save(gamePlayer8);

//			GamePlayer gamePlayer9 = new GamePlayer(game5, player3);
//			gamePlayerRepository.save(gamePlayer9);

			GamePlayer gamePlayer10 = new GamePlayer(game5, player2);
			gamePlayerRepository.save(gamePlayer10);



			List<String> location1 = Arrays.asList("H2", "H3", "H4");
			Ship ship1 = new Ship("destroyer", location1, gamePlayer1);
			shipRepository.save(ship1);

			List<String> location2 = Arrays.asList("E1", "F1", "G1");
			Ship ship2 = new Ship("Submarine", location2, gamePlayer1);
			shipRepository.save(ship2);

			List<String> location3 = Arrays.asList("B4", "B5");
			Ship ship3 = new Ship("Patrol Boat", location3, gamePlayer1);
			shipRepository.save(ship3);

			List<String> location4 = Arrays.asList("B5","C5","D5");
			Ship ship4 = new Ship("destroyer", location4, gamePlayer2);
			shipRepository.save(ship4);

			List<String> location5 = Arrays.asList("F1", "F2");
			Ship ship5 = new Ship("Patrol Boat", location5, gamePlayer2);
			shipRepository.save(ship5);

			List<String> location6 = Arrays.asList("B5","C5","D5");
			Ship ship6 = new Ship("destroyer", location6, gamePlayer3);
			shipRepository.save(ship6);

			List<String> location7 = Arrays.asList("C6","C7");
			Ship ship7 = new Ship("Patrol Boat", location7, gamePlayer3);
			shipRepository.save(ship7);

			List<String> location8 = Arrays.asList("A2","A3","A4");
			Ship ship8 = new Ship("Submarine", location8, gamePlayer4);
			shipRepository.save(ship8);

			List<String> location9 = Arrays.asList("G6","H6");
			Ship ship9 = new Ship("Patrol Boat", location9, gamePlayer4);
			shipRepository.save(ship9);

			List<String> location10 = Arrays.asList("B5","C5","D5");
			Ship ship10 = new Ship("destroyer", location10, gamePlayer5);
			shipRepository.save(ship10);

			List<String> location11 = Arrays.asList("C6","C7");
			Ship ship11 = new Ship("Patrol Boat", location11, gamePlayer5);
			shipRepository.save(ship11);

			List<String> location12 = Arrays.asList("A2","A3","A4");
			Ship ship12 = new Ship("Submarine", location12, gamePlayer6);
			shipRepository.save(ship12);

			List<String> location13 = Arrays.asList("G6","H6");
			Ship ship13 = new Ship("Patrol Boat", location13, gamePlayer6);
			shipRepository.save(ship13);


			List<String> salvo_loc1 = Arrays.asList("B5","C5","F1");
			Salvo salvo1 = new Salvo(1, salvo_loc1, gamePlayer1);
			salvoRepository.save(salvo1);

			List<String> salvo_loc2 = Arrays.asList("B4","B5","B6");
			Salvo salvo2 = new Salvo (1, salvo_loc2, gamePlayer2);
			salvoRepository.save(salvo2);

			List<String> salvo_loc3 = Arrays.asList("A2","A4","G6");
			Salvo salvo3 = new Salvo(2, salvo_loc3, gamePlayer1);
			salvoRepository.save(salvo3);

			List<String> salvo_loc4 = Arrays.asList("E1","H3","A2");
			Salvo salvo4 = new Salvo(2, salvo_loc4, gamePlayer2);
			salvoRepository.save(salvo4);

			Score score1 = new Score(1.0, game1, player1);
			scoreRepository.save(score1);

			Score score2 = new Score(0.0, game1, player2);
			scoreRepository.save(score2);

			Score score3 = new Score(0.5, game2 , player1);
			scoreRepository.save(score3);

			Score score4 = new Score(0.5, game2, player2);
			scoreRepository.save(score4);

			Score score5 = new Score(1.0, game3, player2);
			scoreRepository.save(score5);

			Score score6 = new Score (0.0, game3, player4);
			scoreRepository.save(score6);









		};

	}




}


@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(inputName -> {
			Player player = playerRepository.findByUserName(inputName);
			if (player != null) {
				return new User(player.getUserName(), player.getPassword(),
						AuthorityUtils.createAuthorityList("USER"));
			} else {
				throw new UsernameNotFoundException("UNKNOWN USER:" + inputName);
			}


		});

	}

}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/web/**").permitAll()
				.antMatchers("/api/login").permitAll()
				.antMatchers("/api/create-account").permitAll()
				.antMatchers("/api/scoreboard").permitAll()
				.antMatchers("/rest/**").denyAll()
				.anyRequest().fullyAuthenticated()
				.and().httpBasic();

		http.formLogin()
				.usernameParameter("userName")
				.passwordParameter("password")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");


		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());


	}



	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}



}
}