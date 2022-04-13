from clojure as build
ARG VERSION
RUN mkdir /opt/app
WORKDIR /opt/app
COPY . .
RUN lein bump-version ${VERSION}
RUN lein uberjar
RUN cp target/uberjar/tic-tac-toe-${VERSION}-standalone.jar tic-tac-toe.jar
RUN chgrp root /opt/app && chmod ug+rwX /opt/app && chmod a+rX /opt/app
CMD ["java", "-jar", "tic-tac-toe.jar"]
