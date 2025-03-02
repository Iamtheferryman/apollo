/*
 * Copyright 2021 Apollo Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
directive_module.directive('showtextmodal', showTextModalDirective)
    .filter('jsonBigIntFilter', function () {
        return function (text) {
            if (typeof (text) === "undefined" || typeof JSON.parse(text) !== "object"
                || !text) {
                return;
            }

            const numberRegex = /\d{16,}/g;
            const splitRegex = /"\d\d+"/;
            const splitArray = text.split(splitRegex);
            const matchResult = text.match(numberRegex);

            if (!matchResult) {
                return text;
            }

            let resultStr = '';
            let index = 0;

            Object.keys(splitArray).forEach(function (key) {
                resultStr = resultStr.concat(splitArray[key]);
                if (typeof (matchResult[index]) !== "undefined") {
                    resultStr = resultStr.concat(matchResult[index++])
                }
            })

            return resultStr;
        }
    });


function showTextModalDirective(AppUtil) {
    return {
        restrict: 'E',
        templateUrl: AppUtil.prefixPath() + '/views/component/show-text-modal.html',
        transclude: true,
        replace: true,
        scope: {
            text: '='
        },
        link: function (scope) {
            scope.$watch('text', init);

            function init() {
                scope.jsonObject = undefined;
                if (isJsonText(scope.text)) {
                    scope.jsonObject = parseBigInt(scope.text);
                }
            }

            function isJsonText(text) {
                try {
                    return typeof JSON.parse(text) === "object";
                } catch (e) {
                    return false;
                }
            }

            function parseBigInt(str) {
                if (/\d{16,}/.test(str)) {
                    let replaceMap = [];
                    let n = 0;
                    str = str.replace(/"(\\?[\s\S])*?"/g, function (match) {
                        if (/\d{16,}/.test(match)) {
                            replaceMap.push(match);
                            return '"""';
                        }
                        return match;
                    }).replace(/[+\-\d.eE]{16,}/g, function (match) {
                        if (/^\d{16,}$/.test(match)) {
                            return '"' + match + '"';
                        }
                        return match;
                    }).replace(/"""/g, function () {
                        return replaceMap[n++];
                    })
                }
                return JSON.parse(str);
            }
        }
    }
}


