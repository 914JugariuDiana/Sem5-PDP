#include <iostream>
#include <mutex>
#include <list>
#include <vector>
#include <condition_variable>
#include <thread>


std::mutex mtx;
bool producer_done = false;
std::condition_variable cv;
std::vector<int> vec1;
std::vector<int> vec2;
std::vector<int> products;
int dot_product = 0;

void produce()
{
    for (int i = 0; i < vec1.size(); i++)
    {
        int local_result = vec1.at(i) * vec2.at(i);
        {
            std::unique_lock<std::mutex> lck(mtx);
            products.push_back(local_result);
            std::cout << std::this_thread::get_id() << " product: " << local_result << ", time: " << time(0) << "\n";
            cv.notify_all();
        }
    }
    {
        std::unique_lock<std::mutex> lck(mtx);
        producer_done = true;
        cv.notify_all();
    }
}

void consume()
{
    std::unique_lock<std::mutex> lck(mtx);
    while (true) {
        while (!products.empty()) {
            dot_product += products.back();
            products.pop_back();
            std::cout << std::this_thread::get_id() << " current sum: " << dot_product << ", time: " << time(0) << "\n";
        }
        if (producer_done) {
            break;
        }
        cv.wait(lck);
    }
}

int main()
{
    vec1.push_back(5);
    vec1.push_back(3);
    vec1.push_back(2);
    vec1.push_back(8);
    vec1.push_back(6);

    vec2.push_back(3);
    vec2.push_back(4);
    vec2.push_back(6);
    vec2.push_back(7);
    vec2.push_back(8);

    std::thread producer(produce);
    std::thread consumer(consume);

    producer.join();
    consumer.join();

    std::cout << "Result: " << dot_product;
}



