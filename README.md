# APIDOC PROTECTOR
A simple project to API document protect

# Overview

- Release: 1.0.0

![img.png](./midias/apidoc-protector-login-v1.png)

When APIDOC PROTECTOR is installed in the application it catches the requests into endpoints refers to currently manager
documentation, as example in the Swagger, when is requested to http://localhost:31303/swagger-ui/index.html this request
is intercepted and is required a login by username and password.
<br />
This can be look in ApiDocProtectorSwagger that is placed in the apidocprotector path (package)
in the current application, is very simple just make the correct settings explained below.

> NOTE: All files and scripts is open to review and check the content that each one have, this is will be modified
> in the next releases when will be offers just a jar file as a dependencie, however the source code always stay
> availble to query and warranty secutiry in the use

Below is the diagram flow to summarize as the APIDOC PROTECTOR work.

![img.png](./midias/apidocprotector-diagram-flow.png)

# Resources

* Java 8 (jdk-1.8-212)
* Spring Boot 2.0.1.RELEASE
* Maven Project 2.6.3

# Dependencies

* Lombok
* Spring Data JPA
* Mysql Driver
* Log4j2
* Spring Rest Docs
* Spring Web
* Rest Repositories
* Rest Template
* Swagger (OpenAPI)
* Thymeleaf
* Crypto/MD5

# Environment Details
<pre>

JDK
$ javac -version
javac 1.8.0_212

JRE
$ java -version
java version "1.8.0_212"
Java(TM) SE Runtime Environment (build 1.8.0_212-b10)
Java HotSpot(TM) 64-Bit Server VM (build 25.212-b10, mixed mode)
</pre>

# Maven Commands (jar generate)

1. mvn clean
2. mvn clean install
3. mvn clean compile assembly:single
4. mvn clean package spring-boot:repackage
5. mvn package

# List of Documentation Managers

* OpenAPI with Swagger

> The openAPI with Swagger-UI is available to query and help understand the application by REST API

* http://localhost:31303/swagger-ui/protector

* OpenAPI with Adobe AEM

> The openAPI with Adobe-AEM is available to query and help understand the application by REST API

* http://localhost:31303/adobe-aem/protector

* OpenAPI with Authentiq API

> The openAPI with Authentiq API is available to query and help understand the application by REST API

* http://localhost:31303/authentiq-api/protector

# Workflow Code Details

To clarify and explain with more details about the workflow code, lets started a series of diagrams and the respective 
text about it. In each diagram you can see the initial request until the intecept by APIDOC-PROTECTOR and finally 
arrive in any place previously configured in the application.properties file.

- Generator

In the first diagram we can see the generator flow, where we can see the full process that the request do to get
a html generator page called generator.html.

![img.png](./midias/apidocprotector-diagram-generator.png)

- Activator

The next one diagram are been showed in the below image, and we can see the complete flow to Activator process that refers
to Account Activation after the correct generate Account in the Account Generator of previous step.

![img.png](./midias/apidocprotector-diagram-activator.png)

- Initializer

The initializer process is used always the user need to access the documentation that is protected by APIDOC-PROTECTOR. 
This process should be used as explained in this session, e.g., an URL + Token sended by email to right account access. 

![img.png](./midias/apidocprotector-diagram-initializer.png)

- Swagger Router

![img.png](./midias/apidocprotector-diagram-swagger-router.png)

- Refresh

![img.png](./midias/apidocprotector-diagram-refresh.png)

- Password

![img.png](./midias/apidocprotector-diagram-password.png)

- Password Recovery

![img.png](./midias/apidocprotector-diagram-password-recovery.png)

- Recovery

![img.png](./midias/apidocprotector-diagram-recovery.png)

- Sentinel (Root Path)

![img.png](./midias/apidocprotector-diagram-sentinel-root.png)

- Sentinel (Docs Path)

![img.png](./midias/apidocprotector-diagram-sentinel-docs.png)

- Sentinel (Protect Path)

![img.png](./midias/apidocprotector-diagram-sentinel-protect.png)

- Sentinel (Router Path)

![img.png](./midias/apidocprotector-diagram-sentinel-router.png)

- Sentinel (Logout Path)

![img.png](./midias/apidocprotector-diagram-sentinel-logout.png)

- Sentinel (Protector Path)

![img.png](./midias/apidocprotector-diagram-sentinel-protector.png)

# Usage

Set up the pom.xml with the dependencies below

<code>

		<!--SPRING-BOOT-STARTER-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<!--REST TEMPLATE-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-rest</artifactId>
		</dependency>

		<!--LOMBOK-->
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>

		<!--DATABASE-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<scope>runtime</scope>
		</dependency>

		<!--DOCUMENT/SWAGGER/OPENAPI-->
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-ui</artifactId>
			<version>1.6.4</version>
		</dependency>

		<!--APIDOC-PROTECTOR (REQUIRED)-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>
		<!--APIDOC-PROTECTOR (REQUIRED)-->
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-crypto</artifactId>
            <version>5.6.1</version>
        </dependency>

		<!--MAIL-SENDER-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-mail</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework</groupId>
			<artifactId>spring-context-support</artifactId>
			<version>5.3.15</version>
		</dependency>

</code>

In this release 1.0.0, the unique way to use the APIDOC-PROTECTOR it that showed below, please pay attention when you 
implement this solution in your application to avoid mistakes or forgot anything.

Let's start...

Please, follow the instructions below to install APIDOC PROTECTOR in your application.

- Firstly, access the package com in your application

<pre>
[Example]
user@host$: cd /home/user/sample-project/src/main/java/com
</pre>

- Get the project files from GitHub inside the current path

<pre>
user@host$: git clone https://github.com/huntercodexs/apidoc-protector.git
</pre>

- Get a current release: 1.0.0

<pre>
user@host$: git checkout release_1.0.0
</pre>

- Remove unnecessary files

<pre>
user@host$: cd apidocprotector
user@host$: rm -rf .git .gitignore
user@host$: cd ..
</pre>

Until now, we should have the project structure as below:

![img.png](./midias/apidocprotector-initial-structure.png)

- Copy the template files html from src/main/java/com/apidocprotector/templates to src/main/resources/templates as showed below

![img.png](./midias/apidocprotector-templates-files.png)

- Now you can configure the application.properties file of your project, as showed below:

<pre>
## SWAGGER
#----------------------------------------------------------------------------------------------------
# See more: https://springdoc.org/properties.html
#true, false
springdoc.swagger-ui.enabled=true
#Any path
springdoc.swagger-ui.path=/huntercodexs/sample/swagger-ui
#Ordered
springdoc.swagger-ui.operationsSorter=method
#StandaloneLayout, BaseLayout
springdoc.swagger-ui.layout=StandaloneLayout
#/api-docs, api-docs/sample, /api-docs-custom
springdoc.api-docs.path=/api-docs/sample
#true, false
springdoc.model-and-view-allowed=true

## APIDOC PROTECTOR
#----------------------------------------------------------------------------------------------------
#true, false
apidocprotector.enabled=false
#localhost, 192.168.0.17, app.domain.com
apidocprotector.server-name=192.168.0.203
#swagger, adobe, authentiq
apidocprotector.type=swagger
#md5, bcrypt
apidocprotector.data.crypt.type=md5
#true, false
apidocprotector.url.show=true
#time to expire session (in minutes): 0,1,3,5,6,7, .... 15,60,148, etc...
apidocprotector.session.expire-time=1
#time to expire email to account activation (in minutes): 20,40,60, etc...
apidocprotector.email.expire-time=1
#custom server domain
apidocprotector.custom.server-domain=http://localhost:31303
#custom uri
apidocprotector.custom.uri-login=/doc-protect/login
apidocprotector.custom.uri-logout=/doc-protect/logout
apidocprotector.custom.uri-form=/doc-protect/protector/form
apidocprotector.custom.uri-user-generator=/doc-protect/generator/user
apidocprotector.custom.uri-user-recovery=/doc-protect/recovery/user
apidocprotector.custom.uri-user-password=/doc-protect/password/user
apidocprotector.custom.uri-user-password-recovery=/doc-protect/password/recovery/user
apidocprotector.custom.uri-generator=/doc-protect/generator
apidocprotector.custom.uri-account-active=/doc-protect/account/active
apidocprotector.custom.uri-recovery=/doc-protect/recovery
apidocprotector.custom.uri-password=/doc-protect/password
apidocprotector.custom.uri-password-recovery=/doc-protect/password/recovery

## APIDOC PROTECTOR (JAVA MAIL SENDER)
#----------------------------------------------------------------------------------------------------
### Tip: Use an test mail server as the MailHog (see more in docker-series ion GitHub huntercodexs account)
spring.mail.host=192.168.0.174
spring.mail.port=31025
spring.mail.username=huntercodexs@mail.com
spring.mail.password=
spring.mail.properties.mail.smtp.auth=false
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.connectiontimeout=5000
spring.mail.properties.mail.smtp.timeout=5000
spring.mail.properties.mail.smtp.writetimeout=5000
spring.mail.properties.mail.smtp.socketFactory.port=31025
spring.mail.properties.mail.smtp.socketFactory.class=javax.net.ssl.SSLSocketFactory
</pre>

# Properties File

> NOTE: you can personlize the all options/features to get more confidente and security in the application implementation,
> main in the custome.uri path

There is a many settings tha can be made in the application.properties file, but we can talk about the options on  

<pre>
## APIDOC PROTECTOR
## APIDOC PROTECTOR (JAVA MAIL SENDER)
</pre>

- APIDOC PROTECTOR OPTIONS 
  - apidocprotector.enabled: 
    - Use true or false to enabled/disable the APIDOC PROTECTOR
  - apidocprotector.server-name: 
    - Define where the application is running and will be used by Firewall in the APIDOC PROTECTOR
  - apidocprotector.type: 
    - Define wich kind of OPENAPI will be used, swagger, abode-aem or authentiq
  - apidocprotector.data.crypt.type: 
    - Set the kind of algorithm to data cipher
  - apidocprotector.url.show: 
    - Use true or false to mean if the doc-path url should be view
  - apidocprotector.session.expire-time: 
    - Define the time to session expire (in minutes), if value is 0 then expired time will be disabled
  - apidocprotector.email.expire-time: 
    - Define the time to email expire (in minutes), if value is 0 then expired time will be disabled
  - apidocprotector.custom.server-domain: 
    - Define where the APIDOC PROTECTOR is running 
  - apidocprotector.custom.uri-login: 
    - URI to redirect login form, default is /doc-protect/login 
  - apidocprotector.custom.uri-logout: 
    - URI to redirect user after logout, default is /doc-protect/logout 
  - apidocprotector.custom.uri-form: 
    - URI to redirect user to make a login, default is /doc-protect/protector/form 
  - apidocprotector.custom.uri-user-generator: 
    - URI defined to generator new account, default is /doc-protect/generator/user 
  - apidocprotector.custom.uri-user-recovery: 
    - URI that refer to recovery functionaly, default is /doc-protect/recovery/user 
  - apidocprotector.custom.uri-user-password: 
    - URI to user define a new password, default is /doc-protect/password/user 
  - apidocprotector.custom.uri-user-password-recovery: 
    - URI to user recovery the forgot password, default is /doc-protect/password/recovery/user 
  - apidocprotector.custom.uri-generator: 
    - URI to generate a form generator and generate an new user, default is /doc-protect/generator 
  - apidocprotector.custom.uri-account-active: 
    - URI to active account when it is required by email, default is /doc-protect/account/active 
  - apidocprotector.custom.uri-recovery: 
    - URI to account recovery, default is /doc-protect/recovery 
  - apidocprotector.custom.uri-password: 
    - URI to password, default is /doc-protect/password 
  - apidocprotector.custom.uri-password-recovery: 
    - URI to password recovery, default is /doc-protect/password/recovery

# Database Details

# Account Creation

- User Generator
  - http://localhost:31303/doc-protect/generator
- Acessar email para ativar conta
  - Clicar no link (Activate Account)
- Após clicar no link para ativar a conta
  - Verificar email de boas vindas ao APIDOC PROTECTOR
  - Clicar no link (click here) para acessar a conta
- Acessar a conta (via link enviado por email)
  - http://localhost:31303/doc-protect/protector/form#x-padlock

# Account Recovery

- Acessar formulario para gerar novo usuário
    - http://localhost:31303/doc-protect/generator
- Clicar em Account Recovery
  - http://localhost:31303/doc-protect/recovery/form#x-padlock
  - Informar um email valido (que foi usado para cadastro)
  - Checar o email
  - Clicar no link Activate Account
  - Um novo email será enviado para acessar a conta com um novo token
  - Informar usuario e senha

# Login

> NOTA: Não é possível fazer o login sem passar para formulario de login

- Para acessar o formulario de login é preciso saber qual o link de acesso com o token valido, esse link pode estar 
salvo em seu email, ou então ser recuperado no processo de Recuperação de Conta, exemplo:
- http://localhost:31303/doc-protect/login/287891be-aa00-4422-88cd-b52ae7645d0b

# Password Recovery

- Na tela de login clicar em "change password"
  - http://localhost:31303/doc-protect/password
  - Sera aberta uma tela pedindo o endereço de email
    - Insira o email para inciiar o processo de recuperação de senha
    - Verifique seu email para definir uma nova senha
    - Informe sua nova senha na tela de recuperação de senha
    - Um novo link sera gerado para acesso, exemplo:
      - http://localhost:31303/doc-protect/login/6a27a71d-2d4c-40e0-bcd6-d782db72b9a8
    - Clique no link e efetue o login com a nova senha

#  Account Expired

> Existe tempo para acessar a conta pela primeira vez depois de um usuário gerado pelo formulario gerador de contas, 
> por isso é preciso verificar com atenção o que diz o email sobre o tempo para ativação de conta

# Invalid Account


# Session Expired


# Intercepted flow

# Advanced

- OAuth2 Settings

If your application use the OAuth2 as a security layer, can be needed to make any configurations in the scope of the
project as showed below...

<code>

    @Override
    public void configure(final HttpSecurity http) throws Exception {
       http.authorizeRequests()

               /*APP SERVICES*/
               .antMatchers("/users/delete").authenticated()
               .antMatchers("/users/create").authenticated()

               /*OTHERS SERVICES*/
               .antMatchers("/auditory/view").authenticated()

               /*SWAGGER*/
               .antMatchers("/swagger/**").permitAll()
               .antMatchers("/swagger-ui/**").permitAll()
               .antMatchers("/api-docs/**").permitAll()
               .antMatchers("/api-docs.yaml").permitAll()

               /*API-DOC-GUARD*/
               .antMatchers("/doc-protect/**").permitAll()
               .antMatchers("/api-doc-guard/**").permitAll()
               .antMatchers("/api-docs-guard/**").permitAll()

               /*CUSTOM*/
               .antMatchers(custom_api_prefix+"/swagger/**").permitAll()
               .antMatchers(custom_api_prefix+"/swagger-ui/**").permitAll()
               .antMatchers(custom_api_prefix+"/doc-protect/**").permitAll()
               .antMatchers(custom_api_prefix+"/api-doc-guard/**").permitAll()
               .antMatchers(custom_api_prefix+"/api-docs-guard/**").permitAll()

               /*ACTUATOR*/
               .antMatchers("/actuator/**").permitAll().anyRequest().authenticated();
    
    }

</code>
