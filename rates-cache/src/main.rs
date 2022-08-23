use std::{
    env::{self},
    fs,
};

#[tokio::main]
async fn main() {
    let mut key = env::var_os("APP_ID").unwrap().into_string().unwrap();
    let url = format!("https://openexchangerates.org/api/latest.json?app_id={}",key);
    println!("{}",url);
    let resp = reqwest::get(&url).await.unwrap().text().await.unwrap();
    fs::write("./latest.json", resp.clone()).unwrap();
    println!("{}", resp);
}
